package com.lhjitem.seckilldemo.domain;

import com.lhjitem.seckilldemo.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
//validation这个依赖可以帮助我们简化校验过程
/**
 * @author lhj
 * @create 2021/10/26 23:54
 * 登录参数
 */
@Data
public class CommonLogin {
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
