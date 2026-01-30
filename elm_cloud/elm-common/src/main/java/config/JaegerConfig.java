package config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Jaeger 追踪配置类
 * 通过设置系统属性配置Jaeger Agent地址和采样策略
 * Micrometer Tracing 会自动读取这些属性并创建 Tracer
 */
@Configuration
@ConditionalOnProperty(prefix = "tracing.jaeger", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class JaegerConfig {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @Value("${tracing.jaeger.agent.host:REDACTED_IP}")
    private String jaegerAgentHost;

    @Value("${tracing.jaeger.agent.port:6831}")
    private int jaegerAgentPort;

    @Value("${tracing.jaeger.sampler.type:const}")
    private String samplerType;

    @Value("${tracing.jaeger.sampler.param:1}")
    private Number samplerParam;

    @PostConstruct
    public void configureJaeger() {
        // 设置Jaeger系统属性（Jaeger客户端和Micrometer会自动读取）
        System.setProperty("JAEGER_SERVICE_NAME", serviceName);
        System.setProperty("JAEGER_AGENT_HOST", jaegerAgentHost);
        System.setProperty("JAEGER_AGENT_PORT", String.valueOf(jaegerAgentPort));
        System.setProperty("JAEGER_SAMPLER_TYPE", samplerType);
        System.setProperty("JAEGER_SAMPLER_PARAM", String.valueOf(samplerParam));
        
        log.info("配置 Jaeger - 服务名: {}, Agent: {}:{}, 采样类型: {}, 采样参数: {}", 
                serviceName, jaegerAgentHost, jaegerAgentPort, samplerType, samplerParam);
    }
}