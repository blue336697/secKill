package com.lhjitem.seckilldemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IUserService;
import com.lhjitem.seckilldemo.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author lhj
 * @create 2022/3/11 17:48
 * 接口限流的拦截器
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null)
                return true;
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();
            if(needLogin){
                if(user == null) {
                    //如果为空就返回异常对象
                    render(response, CommonResult.LOGIN_EXCEPTION);
                    return false;
                }
                uri+=":"+user.getId();

            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(uri);
            if(count == null)
                valueOperations.set(uri,1,second,TimeUnit.SECONDS);
            else if(count < maxCount)
                valueOperations.increment(uri);
            else {
                render(response, CommonResult.ACCESS_LIMIT);
                return false;
            }

        }
        return true;
    }


    /**
     * 构建返回对象
     * @param response
     * @param loginException
     */
    private void render(HttpServletResponse response, CommonResult loginException) throws Exception{
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        CommonBean commonBean = CommonBean.error(loginException);
        writer.write(new ObjectMapper().writeValueAsString(commonBean));
        writer.flush();
        writer.close();
    }


    /**
     * 把原先UserArgumentResolver的resolveArgument提取封装成一个通过threadLocal获取user
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request,HttpServletResponse response){
        String ticket = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(ticket))
            return null;
        return userService.getUserByCookie(ticket,request,response);
    }
}
