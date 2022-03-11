package com.lhjitem.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.domain.CommonOrder;
import com.lhjitem.seckilldemo.pojo.Order;
import com.lhjitem.seckilldemo.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param commonGood
     * @return
     */
    Order seckill(User user, CommonGood commonGood);

    CommonOrder detail(Long orderId);

    String createPath(User user, Long goodsId);


    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
