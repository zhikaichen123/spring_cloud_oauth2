package com.microservice.skeleton.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceApplication {

	public static void main(String[] args) {
		System.setProperty("spring.cloud.zookeeper.discovery.register", "false");
		SpringApplication.run(ResourceApplication.class, args);
	}
}
