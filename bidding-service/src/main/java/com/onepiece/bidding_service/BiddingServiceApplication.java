package com.onepiece.bidding_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class BiddingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiddingServiceApplication.class, args);
	}
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder
//                .requestFactory(() -> {
//                    org.springframework.http.client.HttpComponentsClientHttpRequestFactory factory =
//                            new org.springframework.http.client.HttpComponentsClientHttpRequestFactory();
//                    factory.setConnectTimeout(Duration.ofSeconds(5));
//                    factory.setReadTimeout(Duration.ofSeconds(10));
//                    return factory;
//                })
//                .build();
//    }
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
}
