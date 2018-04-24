package com.microservice.skeleton.register.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @describe 监控服务心跳
 * @author zhikai.chen
 * @date 2018年4月23日 下午3:25:50
 */
@Configuration
public class InstanceRenewListener implements ApplicationListener<EurekaInstanceRenewedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceRenewListener.class);

	public void onApplicationEvent(EurekaInstanceRenewedEvent event) {
		LOGGER.info("心跳检测服务：{}" ,event.getInstanceInfo().getAppName());
	}
   
}
