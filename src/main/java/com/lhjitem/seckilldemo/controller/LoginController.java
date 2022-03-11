package com.lhjitem.seckilldemo.controller;

import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonLogin;
import com.lhjitem.seckilldemo.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author lhj
 * @create 2021/10/26 22:16
 * 登录
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private IUserService userService;

    //跳转登录页面
    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }


    //登录功能
    @RequestMapping("/doLogin")
    @ResponseBody
    public CommonBean doLogin(@Valid CommonLogin login, HttpServletRequest request, HttpServletResponse response){

        return userService.doLogin(login,request,response);
    }
}
