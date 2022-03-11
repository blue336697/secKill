package com.lhjitem.seckilldemo.config;

import com.lhjitem.seckilldemo.pojo.User;

/**
 * @author lhj
 * @create 2022/3/11 17:55
 * ThreadLocal解决的是每个线程绑定自己的值，如果在一个公共线程中存放用户信息会导致用户信息的紊乱
 * 所以我们需要每个登录的用户的信息都存放在自己的线程中，从而避免线程安全问题
 * ThreadLocal是Thread的一个字段，初始值为null，当调用get、set方法时会被初始化得到，
 * 内部存放的结构是hashmap即ThreadLocalMap，key是当前线程，value就是set进行的对象
 */
public class UserContext {
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user){
        userThreadLocal.set(user);
    }

    public static User getUser(){
        return userThreadLocal.get();
    }
}
