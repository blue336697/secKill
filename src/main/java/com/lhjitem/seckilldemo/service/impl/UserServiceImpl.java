package com.lhjitem.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonLogin;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.exception.GlobalException;
import com.lhjitem.seckilldemo.mapper.UserMapper;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IUserService;
import com.lhjitem.seckilldemo.utils.CookieUtil;
import com.lhjitem.seckilldemo.utils.MD5Util;
import com.lhjitem.seckilldemo.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author duoduo
 * @since 2021-10-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public CommonBean doLogin(CommonLogin login, HttpServletRequest request, HttpServletResponse response) {
        String mobile = login.getMobile();
        String password = login.getPassword();
        /*对于这些校验逻辑虽然必要但重复性太高，我们可以引入<!-- validation组件->来帮助我们简化代码*/
        //参数校验
        /*if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            return CommonBean.error(CommonResult.LOGIN_ERROR);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return CommonBean.error(CommonResult.MOBILE_ERROR);
        }*/
        //根据用户手机号获取用户
        User user = userMapper.selectById(mobile);
        if(user==null){
//            return CommonBean.error(CommonResult.LOGIN_ERROR);
            throw new GlobalException(CommonResult.LOGIN_ERROR);
        }
        //用户名没问题就开始校验密码
        if(!MD5Util.fromPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
//            return CommonBean.error(CommonResult.PASSWORD_ERROR);
            throw new GlobalException(CommonResult.PASSWORD_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();

        //由于要实现分布式session，这里就不放对象到session中
//        request.getSession().setAttribute(ticket,user);

        //将用户信息存入到redis中，解决分布式session中用户不一致的问题
        redisTemplate.opsForValue().set("user:"+ticket,user);
        CookieUtil.writeLoginToken(request,response,ticket);
        return CommonBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String ticket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(ticket)){
             return null;
        }
        //将用户信息存入到redis中，解决分布式session中用户不一致的问题，这里就相当于缓存了
        //这就是我们俗称的对象缓存，相对于页面缓存更加细粒度了
        //细粒度模型,通俗的讲就是将业务模型中的对象加以细分,
        //从而得到更科学合理的对象模型,直观的说就是划分出很多对象.
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user!=null)
            //这样可以更加保险
            CookieUtil.writeLoginToken(request,response,ticket);
        return user;
    }

    @Override
    public CommonBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(user == null)
            throw new GlobalException(CommonResult.MOBILE_NOT_EXIST);
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int res = userMapper.updateById(user);
        if(1 == res) {
            //更新成功就要去更新redis
            redisTemplate.delete("user:" + userTicket);
            return CommonBean.success();
        }
        return CommonBean.error(CommonResult.PASSWORD_UPDATE_FAIL);
    }


}
