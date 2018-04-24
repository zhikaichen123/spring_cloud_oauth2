package com.microservice.skeleton.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @describe 网页入口
 * @author zhikai.chen
 * @date 2018年4月23日 下午3:26:43
 */
@RestController
public class UserController {

    @GetMapping(value = "getUser")
    public String getUser(){
        return "order";
    }

}
