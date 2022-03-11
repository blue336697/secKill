package com.lhjitem.seckilldemo.domain;

import lombok.*;

/**
 * @author lhj
 * @create 2021/10/26 23:34
 */
@ToString
@AllArgsConstructor
@Getter
public enum CommonResult {
    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务器异常"),

    //登录模块
    LOGIN_ERROR(120,"用户名或密码错误"),
    MOBILE_ERROR(121,"用户名（手机号）格式错误"),
    PASSWORD_ERROR(122,"密码错误"),
    BIND_ERROR(123,"参数绑定异常"),
    MOBILE_NOT_EXIST(124,"手机号码不存在！"),
    PASSWORD_UPDATE_FAIL(125,"更新密码失败！"),
    LOGIN_EXCEPTION(126,"登录异常或用户不存在，请重新登录！"),

    //购物模块
    Stock_Error(220,"库存不足"),
    Order_Error(221,"不能重复抢购哦"),
    REQUEST_ILLEGAL(222,"非法请求，请重新尝试"),
    CAPTCHA_ILLEGAL(223,"验证码错误，请重新输入"),
    ACCESS_LIMIT(224,"访问过于频繁，请稍候再试"),

    //订单模块
    Order_NOT_EXIST(320,"订单不存在");

    private final Integer code;
    private final String message;
}
