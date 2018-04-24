/**
 * 
 */
package com.microservice.skeleton.gateway.api;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @describe 用户验证接口
 * @author zhikai.chen
 * @date 2018年4月23日 上午9:16:59
 */
@FeignClient("authCenter")
public interface IOauth {
	
	/**
	 * 请求验证
	 * @param access_token 验证码
	 * @param sourcePath 请求地址
	 * @return 结果
	 */
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    @ResponseBody
    Map<String,String> check(@RequestParam("access_token") String access_token, @RequestParam("sourcePath") String sourcePath);
    
}
