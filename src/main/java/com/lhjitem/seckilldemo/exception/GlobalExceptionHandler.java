package com.lhjitem.seckilldemo.exception;

import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lhj
 * @create 2021/10/27 18:25
 * 全局异常处理类:RestControllerAdvice（只能拦截controller接收到的请求以后产生的注解）+ExceptionHandler（可以捕获全部异常）这两个组合注解来处理异常
 */


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public CommonBean ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException ge = (GlobalException) e;
            return CommonBean.error(ge.getCommonResult());
        }else if(e instanceof BindException){
            BindException be = (BindException) e;
            CommonBean commonBean = CommonBean.error(CommonResult.BIND_ERROR);
            commonBean.setMessage("参数校验异常："+be.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return commonBean;
        }
        return CommonBean.error(CommonResult.ERROR,e.getStackTrace());
    }
}

