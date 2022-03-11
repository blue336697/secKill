package com.lhjitem.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonLogin;
import com.lhjitem.seckilldemo.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duoduo
 * @since 2021-10-26
 */
public interface IUserService extends IService<User> {

    /**
     * 登录
     * @param login
     * @param request
     * @param response
     * @return
     */
    CommonBean doLogin(CommonLogin login, HttpServletRequest request, HttpServletResponse response);


    /**
     * 通过cookie获取redis中的user
     * @param ticket
     * @return
     */
    User getUserByCookie(String ticket,HttpServletRequest request,HttpServletResponse response);

    /**
     * 更新密码，这里就要体现对于这个用户来说，这个用户的信息的更新评率肯定是很慢的，但我们一直将他存在缓存中并且设置成了永不过期
     * 所以如果你没有这个更新密码，那么用户更新完信息，拿到的信息是从redis来的那么肯定是旧的
     * 这时就要考虑mysql的信息和redis的信息怎么保持一致呢
     * 其实很简单：就是对于mysql的操作对应redis的信息全部清空在初始化，这样就可以保持信息的一致性了
     * @param userTicket
     * @param password
     * @return
     */
    CommonBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);



}
