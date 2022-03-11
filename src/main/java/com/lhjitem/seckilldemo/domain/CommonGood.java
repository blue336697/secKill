package com.lhjitem.seckilldemo.domain;

import com.lhjitem.seckilldemo.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lhj
 * @create 2021/11/5 22:33
 * 该类就是解决秒杀商品类中字段能显示商品全部信息而创建的，能够将商品的全部信息封装成一个通用商品类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonGood extends Goods { //继承goods就可以少写字段了

    private BigDecimal seckillPrice;    //秒杀夹克

    private Integer stockCount;     //库存

    private Date startDate;

    private Date endDate;
}
