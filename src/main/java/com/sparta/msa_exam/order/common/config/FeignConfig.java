package com.sparta.msa_exam.order.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> requestTemplate.header("X-Feign-Client", "x-client-secret");
    }
}