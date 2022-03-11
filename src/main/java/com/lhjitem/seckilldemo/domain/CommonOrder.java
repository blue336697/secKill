package com.lhjitem.seckilldemo.domain;

import com.lhjitem.seckilldemo.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author lhj
 * @create 2022/3/8 20:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonOrder {
    private Order order;

    private CommonGood commonGood;
}
