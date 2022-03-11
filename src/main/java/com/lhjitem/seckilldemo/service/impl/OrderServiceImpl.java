package com.lhjitem.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.domain.CommonOrder;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.exception.GlobalException;
import com.lhjitem.seckilldemo.mapper.OrderMapper;
import com.lhjitem.seckilldemo.pojo.Order;
import com.lhjitem.seckilldemo.pojo.SeckillGoods;
import com.lhjitem.seckilldemo.pojo.SeckillOrder;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IGoodsService;
import com.lhjitem.seckilldemo.service.IOrderService;
import com.lhjitem.seckilldemo.service.ISeckillGoodsService;
import com.lhjitem.seckilldemo.service.ISeckillOrderService;
import com.lhjitem.seckilldemo.utils.MD5Util;
import com.lhjitem.seckilldemo.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 秒杀
     * @param user
     * @param commonGood
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, CommonGood commonGood) {
        //获得秒杀商品
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", commonGood.getId()));
        //减少秒杀商品的库存
        seckillGoods.setStockCount(commonGood.getStockCount()-1);
        //更新库存
//        seckillGoodsService.updateById(seckillGoods);
        //现在来解决库存超卖问题
        boolean seckillRes = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1").eq("goods_id",commonGood.getId()).gt("stock_count",0));

        //判断是否还有数据库库存
        if(seckillGoods.getStockCount() < 1){
            redisTemplate.opsForValue().set("isStockEmpty:"+commonGood.getId(),"0");
            return null;
        }


        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(commonGood.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(commonGood.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        //未支付
        order.setStatus(0);
        order.setCreateDate(new Date());

        //生成订单
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(commonGood.getId());

        seckillOrderService.save(seckillOrder);
        //将订单信息存入redis中
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+commonGood.getId(),
                seckillOrder);

        return order;
    }

    @Override
    public CommonOrder detail(Long orderId) {
        if(orderId == null)
            throw new GlobalException(CommonResult.Order_NOT_EXIST);
        Order order = orderMapper.selectById(orderId);
        CommonGood good = goodsService.findGoodsByGoodsId(order.getGoodsId());
        CommonOrder commonOrder = new CommonOrder();
        commonOrder.setOrder(order);
        commonOrder.setCommonGood(good);
        return commonOrder;
    }

    /**
     * 隐藏秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "200627");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 检验第二层秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user == null || goodsId < 0 || StringUtils.isEmpty(path))
            return false;
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     * 检验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user == null || goodsId < 0 || StringUtils.isEmpty(captcha))
            return false;
        String check = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(check);
    }


}
