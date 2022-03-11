package com.lhjitem.seckilldemo.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 操作浏览器中cookie对象数组
 */
public class CookieUtil {
    //cookie的域名常量
    private final static String COOKIE_DOMAIN = "localhost";
    //cookie的名字常量
    private final static String COOKIE_NAME = "login_token";

    /**
     * 获取存放在浏览器中的cookie对象数组中login_token对应的值
     *
     * @param request 请求对象
     * @return cookie中login_token存的值
     */
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 将生成的token对象存入到浏览器中
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param token    将要存入的浏览器中token对象
     */
    public static void writeLoginToken(HttpServletRequest request, HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        //设置cookie对应的域名
        ck.setDomain(COOKIE_DOMAIN);
        //代表设置在根目录
        ck.setPath("/");
        ck.setHttpOnly(true);
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效
        //单位是秒，如果是-1，代表永久
        ck.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(ck);
    }

    /**
     * 删除浏览器中的cookie中对应的对象数据
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    //设置成0，代表删除此cookie
                    ck.setMaxAge(0);
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}

