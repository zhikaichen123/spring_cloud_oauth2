package com.microservice.skeleton.register.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @describe 监控服务挂了
 * @author zhikai.chen
 * @date 2018年4月23日 下午3:25:13
 */
@Configuration
public class InstanceCancelListener implements ApplicationListener<EurekaInstanceCanceledEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCancelListener.class);

	public void onApplicationEvent(EurekaInstanceCanceledEvent event) {
		LOGGER.info("服务:{}挂了",event.getAppName());
	}
}
