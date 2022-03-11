package com.lhjitem.seckilldemo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lhj
 * @create 2021/10/26 23:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonBean {
    private long code;
    private String message;
    private Object obj;

    /*
    * 成功返回结果
    * */
    public static CommonBean success(){
        return new CommonBean(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getMessage(),null);
    }

    public static CommonBean success(Object o){
        return new CommonBean(CommonResult.SUCCESS.getCode(), CommonBean.success().getMessage(),o);
    }

    /*
     * 失败返回结果
     * */
    public static CommonBean error(CommonResult result){
        return new CommonBean(result.getCode(), result.getMessage(),null);
    }

    public static CommonBean error(CommonResult result,Object o){
        return new CommonBean(result.getCode(), result.getMessage(),o);
    }
}
