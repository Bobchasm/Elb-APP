package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    public ReadWriteSplitInterceptor readWriteSplitInterceptor() {
        return new ReadWriteSplitInterceptor();
    }
}
