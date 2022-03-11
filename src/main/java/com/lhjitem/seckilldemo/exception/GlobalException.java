package com.lhjitem.seckilldemo.exception;

import com.lhjitem.seckilldemo.domain.CommonResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lhj
 * @create 2021/10/27 18:24
 * 全局异常捕获，可以捕获所有异常，未进入controller的异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{
    private CommonResult commonResult;
}
