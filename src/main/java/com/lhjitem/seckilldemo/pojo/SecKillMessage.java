package com.lhjitem.seckilldemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lhj
 * @create 2022/3/10 22:23
 * 这个封装使用rbmq发送订单信息用以生成订单时所需要的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillMessage {
    private User user;
    private Long goodsId;
}
