package com.microservice.skeleton.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableZuulProxy
public class GatewayApplication {

	public static void main(String[] args) {
		System.setProperty("spring.cloud.zookeeper.discovery.register", "false");
		SpringApplication.run(GatewayApplication.class, args);
	}
}
