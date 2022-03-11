package com.lhjitem.seckilldemo.domain;

import com.lhjitem.seckilldemo.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lhj
 * @create 2022/3/7 23:06
 * 做页面静态化时我们需要将变更的数据封装成一个对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonDetail {
    private User user;

    private CommonGood commonGood;

    private int secKillStatus;

    private int remainSeconds;
}
