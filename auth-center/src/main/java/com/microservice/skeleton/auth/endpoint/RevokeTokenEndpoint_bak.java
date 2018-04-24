package com.microservice.skeleton.auth.endpoint;

import com.microservice.skeleton.auth.model.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <br/>注销
 * <br/>Created by Mr.Yangxiufeng on 2017/12/28.
 * <br/>update by zhikai.chen on 2018/04/XX.
 */
@FrameworkEndpoint
public class RevokeTokenEndpoint_bak {
	
    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    //@RequestMapping(value = "/oauth/remove", method= RequestMethod.DELETE)
    @RequestMapping(value = "/oauth/remove", method= RequestMethod.GET)
    public @ResponseBody
    Msg revokeToken(String access_token){
        Msg msg = new Msg();
        if (consumerTokenServices.revokeToken(access_token)){
            msg.setCode(Msg.SUCCESS);
            msg.setMsg("注销成功");
        }else {
            msg.setCode(Msg.FAILED);
            msg.setMsg("注销失败");
        }
        return msg;
    }
    
}
