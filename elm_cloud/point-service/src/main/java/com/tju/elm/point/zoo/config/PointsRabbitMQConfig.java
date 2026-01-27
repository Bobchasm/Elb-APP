package com.tju.elm.point.zoo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Points服务专用的RabbitMQ配置
 * 职责：配置积分兑换相关的队列、交换机等
 * 注意：这个配置只在points服务中生效
 */
@Configuration
public class PointsRabbitMQConfig {

    // ========== 积分兑换相关配置 ==========
    public static final String POINTS_EXCHANGE_TOPIC_EXCHANGE = "points.exchange.topic";
    public static final String POINTS_EXCHANGE_QUEUE = "points.exchange.queue";
    public static final String POINTS_EXCHANGE_DLQ = "points.exchange.dlq";
    public static final String POINTS_EXCHANGE_ROUTING_KEY = "points.exchange.prededuct";
    public static final String POINTS_EXCHANGE_RETRY_ROUTING_KEY = "points.exchange.retry";

    // ========== 积分增减相关配置（如果需要） ==========
    public static final String POINTS_CHANGE_QUEUE = "points.change.queue";
    public static final String POINTS_CHANGE_ROUTING_KEY = "points.change.#";

    /**
     * 积分兑换Topic交换机
     */
    @Bean
    public TopicExchange pointsExchangeTopicExchange() {
        return new TopicExchange(POINTS_EXCHANGE_TOPIC_EXCHANGE, true, false);
    }

    /**
     * 积分兑换队列（带死信配置）
     */
    @Bean
    public Queue pointsExchangeQueue() {
        return QueueBuilder.durable(POINTS_EXCHANGE_QUEUE)
                .withArgument("x-dead-letter-exchange", "") // 死信到默认交换机
                .withArgument("x-dead-letter-routing-key", POINTS_EXCHANGE_DLQ)
                .withArgument("x-message-ttl", 10000) // 消息10秒过期
                .maxLength(10000) // 队列最大长度
                .build();
    }

    /**
     * 积分兑换死信队列
     */
    @Bean
    public Queue pointsExchangeDLQueue() {
        return QueueBuilder.durable(POINTS_EXCHANGE_DLQ)
                .maxLength(1000) // 死信队列最大长度
                .build();
    }

    /**
     * 积分兑换重试队列（延迟队列）
     */
    @Bean
    public Queue pointsExchangeRetryQueue() {
        return QueueBuilder.durable("points.exchange.retry.queue")
                .withArgument("x-dead-letter-exchange", POINTS_EXCHANGE_TOPIC_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", POINTS_EXCHANGE_ROUTING_KEY)
                .withArgument("x-message-ttl", 5000) // 默认5秒延迟
                .build();
    }

    /**
     * 绑定积分兑换队列到交换机
     */
    @Bean
    public Binding pointsExchangeBinding() {
        return BindingBuilder.bind(pointsExchangeQueue())
                .to(pointsExchangeTopicExchange())
                .with(POINTS_EXCHANGE_ROUTING_KEY);
    }

    /**
     * 绑定重试队列到交换机
     */
    @Bean
    public Binding pointsExchangeRetryBinding() {
        return BindingBuilder.bind(pointsExchangeRetryQueue())
                .to(pointsExchangeTopicExchange())
                .with(POINTS_EXCHANGE_RETRY_ROUTING_KEY);
    }

    /**
     * 积分增减队列（用于接收订单系统发送的积分增减消息）
     */
    @Bean
    public Queue pointsChangeQueue() {
        return new Queue(POINTS_CHANGE_QUEUE, true);
    }

    /**
     * 积分增减Topic交换机
     */
    @Bean
    public TopicExchange pointsChangeTopicExchange() {
        return new TopicExchange("points.change.topic", true, false);
    }

    /**
     * 绑定积分增减队列
     */
    @Bean
    public Binding pointsChangeBinding() {
        return BindingBuilder.bind(pointsChangeQueue())
                .to(pointsChangeTopicExchange())
                .with(POINTS_CHANGE_ROUTING_KEY);
    }

    /**
     * Points服务专用的RabbitTemplate
     * 可以设置不同的序列化方式或配置
     */
    @Bean
    public RabbitTemplate pointsRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(pointsMessageConverter());
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 消息发送失败处理
                System.err.println("消息发送失败: " + cause);
            }
        });
        template.setReturnsCallback(returned -> {
            // 消息路由失败处理
            System.err.println("消息路由失败: " + returned.getReplyText());
        });
        return template;
    }

    /**
     * Points服务专用的消息转换器
     */
    @Bean
    public MessageConverter pointsMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Points服务专用的监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory pointsListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(pointsMessageConverter());
        factory.setConcurrentConsumers(3); // 并发消费者数量
        factory.setMaxConcurrentConsumers(10); // 最大并发消费者数量
        factory.setPrefetchCount(10); // 每次预取数量
        factory.setDefaultRequeueRejected(false); // 拒绝的消息不重新入队
        return factory;
    }
}