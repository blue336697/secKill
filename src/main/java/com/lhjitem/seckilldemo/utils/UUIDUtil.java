package com.lhjitem.seckilldemo.utils;

import java.util.UUID;

/**
 * @author lhj
 * @create 2021/10/27 19:57
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
