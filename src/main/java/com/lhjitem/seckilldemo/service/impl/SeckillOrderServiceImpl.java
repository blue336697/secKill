package com.lhjitem.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhjitem.seckilldemo.mapper.OrderMapper;
import com.lhjitem.seckilldemo.mapper.SeckillOrderMapper;
import com.lhjitem.seckilldemo.pojo.Order;
import com.lhjitem.seckilldemo.pojo.SeckillOrder;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 如果有orderId就成功，-1失败,0：排队中
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null)
            return seckillOrder.getOrderId();
        else if(redisTemplate.hasKey("isStockEmpty:"+goodsId))
            return -1L;
        else
            return 0L;
    }
}
