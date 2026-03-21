package com.tju.elm.notification.zoo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {
    
    private String apiKey;
    
    private String baseUrl;
    
    private String chatEndpoint;
    
    private String model;
    
    private Integer maxTokens;
    
    private Double temperature;
    
    private Double topP;
    
    private Integer timeoutSeconds;
    
    private Integer maxRetries;
}
