package com.lhjitem.seckilldemo.config;

import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IUserService;
import com.lhjitem.seckilldemo.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lhj
 * @create 2021/11/2 22:24
 * 自定义用户参数
 */


@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        //看这个类型是不是我们的user类型
        return parameterType == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        //通过ThreadLocal来获取user

        return UserContext.getUser();



        //下面全部被AccessLimitInterceptor和UserContext所替代

        //由于我们要获取cookie需要request、response，所以通过webRequest来获取
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//
//        HttpServletResponse response = nativeWebRequest.getNativeRequest(HttpServletResponse.class);
//        String ticket = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(ticket))
//            return null;
//        return userService.getUserByCookie(ticket,request,response);
    }
}
