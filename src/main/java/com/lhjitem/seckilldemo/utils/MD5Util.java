package com.lhjitem.seckilldemo.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * @author lhj
 * @create 2021/10/26 20:48
 *
 */
@Component
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //我们自定义的字段加入到加密中
    private static final String salt = "l515h5j1";

    //第一次加密
    public static String inputPassToFromPass(String password){
        String src = ""+salt.charAt(0)+salt.charAt(5)+password+salt.charAt(3);
        return md5(src);
    }

    //第二次加密，使用数据库中的salt进行
    public static String fromPassToDBPass(String password,String salt){
        String src = ""+salt.charAt(3)+salt.charAt(1)+password+salt.charAt(2);
        return md5(src);
    }

    //获取加密两次以后密码的方法
    public static String inputPassToDBPass(String password,String salt){
        String fromPass = inputPassToFromPass(password);
        String dbPass = fromPassToDBPass(fromPass,salt);
        return dbPass;
    }


    /*public static void main(String[] args) {
        //1065e01aa32dc9ce321a0cc0ceea2342
        System.out.println(inputPassToFromPass("200627"));

//        //068deff23f0c161b9e380197f7d10f80
        System.out.println(fromPassToDBPass("1065e01aa32dc9ce321a0cc0ceea2342","l1h2j3"));
//
//        //0644fab12244bb05623336a2ca00fc7d
        System.out.println(inputPassToDBPass("200627","l1h2j3"));
    }*/
}
