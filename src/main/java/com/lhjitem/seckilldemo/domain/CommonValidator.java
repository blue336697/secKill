package com.lhjitem.seckilldemo.domain;

import com.lhjitem.seckilldemo.utils.ValidatorUtil;
import com.lhjitem.seckilldemo.validator.IsMobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author lhj
 * @create 2021/10/27 17:49
 * 指定通过注解指定规则的手机号码校验规则类
 */
public class CommonValidator implements ConstraintValidator<IsMobile,String> {

    //是否为必须要填的值
    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required)
            return ValidatorUtil.isMobile(value);
        else {
            if (StringUtils.isEmpty(value))
                return true;
            else
                return ValidatorUtil.isMobile(value);
        }
    }
}
