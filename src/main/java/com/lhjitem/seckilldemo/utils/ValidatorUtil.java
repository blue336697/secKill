package com.lhjitem.seckilldemo.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lhj
 * @create 2021/10/27 13:26
 * 后端检验的工具类
 */
public class ValidatorUtil {


    private static final Pattern mobile_pattern=Pattern.compile("^1(?:3\\d|4[4-9]|5[0-35-9]|6[67]|7[013-8]|8\\d|9\\d)\\d{8}$");
//    private static final Pattern mobile_pattern=Pattern.compile("[1]([3-9])[0-9]{9}$");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
