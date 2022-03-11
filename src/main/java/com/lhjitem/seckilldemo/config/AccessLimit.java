package com.lhjitem.seckilldemo.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lhj
 * @create 2022/3/11 17:45
 * 这个注解就是整合拦截器，来封装接口限流之计数器算法的代码
 */
@Retention(RetentionPolicy.RUNTIME) //这个注解表示我们自定义的注解是运行时可以运行
@Target(ElementType.METHOD)     //注解的适用范围
public @interface AccessLimit {
    int second();
    int maxCount();
    boolean needLogin() default true;

}
