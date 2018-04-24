package com.microservice.skeleton.gateway.filter;

import com.microservice.skeleton.gateway.api.IOauth;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @describe 统一验证
 * @author zhikai.chen
 * @date 2018年4月23日 上午9:16:17
 */
@Component
public class PreRequestFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PreRequestFilter.class);
    
    private static final String X_TOKEN = "access_token";
    
    @Autowired
    private IOauth oauth;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        LOG.info("send {} request to {}",request.getMethod(),request.getRequestURL().toString());
        String sourcePath=request.getServletPath();
        LOG.info("sourcePath: "+sourcePath);
        
	    //此处可对请求方法进行刷选,请求授权跟登陆都过滤
	    if(request.getRequestURI().startsWith("/uaa")){
	    	return null;
	    }
	    // 先取Header中X-Token
        String accessToken = request.getHeader(X_TOKEN);
        // 如果令牌为空, 再取Cookie中X-Token
        if (StringUtils.isBlank(accessToken)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (X_TOKEN.equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        // 如果令牌为空, 再取QueryString中X-Token
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(X_TOKEN);
        }
        
        boolean ok=true;
        String message = null;
        if (StringUtils.isNotBlank(accessToken)) {
            LOG.info("accessToken: "+accessToken);
            //TODO 自己统一验证
            try{
            	Map<String,String> result=oauth.check(accessToken,sourcePath);
            	int code=Integer.parseInt(result.get("code"));
            	String info=result.get("info");
            	LOG.info("result: "+info);
            	if(code!=0){
            		ok=false;
            		message=info;
            	}
    		}catch(Exception e){
    			ok=false;
    			message=e.getMessage();
    			e.printStackTrace();
    		}
        } 
        
        if(ok){
        	ctx.setSendZuulResponse(true); //对请求进行路由
            ctx.setResponseStatusCode(200);
            ctx.set("isSuccess", true);
        }else {
            ctx.setSendZuulResponse(false); //不对其进行路由
            ctx.setResponseStatusCode(400);
            ctx.setResponseBody(message);
            ctx.set("isSuccess", false);
        }
        return null;
    }
}
