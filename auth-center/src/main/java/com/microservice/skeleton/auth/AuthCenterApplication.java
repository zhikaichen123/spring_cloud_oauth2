package com.microservice.skeleton.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthCenterApplication {

	public static void main(String[] args) {
		System.setProperty("spring.cloud.zookeeper.discovery.register", "false");
		SpringApplication.run(AuthCenterApplication.class, args);
	}
}
