package com.lhjitem.seckilldemo.controller;


import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author duoduo
 * @since 2021-10-26
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 简单的压力测试不同用户的情况
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public CommonBean test(User user){
        return CommonBean.success(user);
    }

}
