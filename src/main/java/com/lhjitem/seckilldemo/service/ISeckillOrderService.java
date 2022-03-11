package com.lhjitem.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lhjitem.seckilldemo.pojo.SeckillOrder;
import com.lhjitem.seckilldemo.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
