package com.microservice.skeleton.auth.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @describe 用户认证具体配置
 * @author zhikai.chen
 * @date 2018年4月20日 下午5:34:44
 */
@RestController
public class UserController {
	
	/**
	 * 获取参数值
	 * @param request 请求对象
	 * @param param 参数名称
	 * @return 对应的值
	 */
	private String getSession(HttpServletRequest request,String param){
		String data = request.getHeader(param);
        if (StringUtils.isBlank(data)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (param.equals(cookie.getName())) {
                    	data = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (StringUtils.isBlank(data)) {
        	data = request.getParameter(param);
        }
        return data;
	}
	
	/**
	 * 完全自主控制权限
	 * @param user 合法用户
	 * @return 验证结果
	 */
	@RequestMapping("/user")
    public Map<String,String> user(Principal user,HttpServletRequest request) {
		//获取访问原目的
        String sourcePath = getSession(request, "sourcePath");
    	String name=user.getName();
    	String next=sourcePath.substring(1);
    	String service=next.substring(0, next.indexOf("/"));
    	System.out.println(name+" -> "+sourcePath+" -> "+service);
    	
    	//TODO 权限检查
    	Map<String,String> map = new LinkedHashMap<>();
    	map.put("code", "-1");
    	map.put("info", "power permission!");
    	OAuth2Authentication authentication = (OAuth2Authentication) user;
    	Collection<GrantedAuthority> authorities=authentication.getAuthorities();
    	for(GrantedAuthority authoritie:authorities){
//    		//暂时需要完全匹配
//    		if(authoritie.getAuthority().equals(sourcePath.trim())){
//    			map.put("code", "0");
//    			map.put("info", "welcome to visit!");
//    			break;
//    		}
    		//服务权限
    		if(authoritie.getAuthority().equals(service.trim())){
			   map.put("code", "0");
			   map.put("info", "welcome to visit!");
			   break;
		    }
    	}
    	return map;
    }
	
	/**
	 * 需要用户权限
	 * @param principal 普通用户
	 * @return 权限
	 */
	@PreAuthorize("hasRole('ROLE_admin') or hasRole('ROLE_user')")
    @RequestMapping("/user2")
    public Map<String,String> user2(Principal principal) {
       Map<String,String> map = new LinkedHashMap<>();
       map.put("name", principal.getName());
       return map;
    }
	
	/**
	 * 需要管理员权限
	 * @param principal 管理员用户
	 * @return 权限
	 */
    @PreAuthorize("hasRole('ROLE_admin')")  
    //@PreAuthorize("#oauth2.hasScope('openid') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "admin", method = RequestMethod.GET)
    public Principal helloSecret(Principal principal) {
    	return principal;
    }

}
