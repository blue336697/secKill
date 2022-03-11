package com.lhjitem.seckilldemo.rabbitmq;

import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.pojo.SecKillMessage;
import com.lhjitem.seckilldemo.pojo.SeckillOrder;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IGoodsService;
import com.lhjitem.seckilldemo.service.IOrderService;
import com.lhjitem.seckilldemo.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author lhj
 * @create 2022/3/10 20:02
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

    /**
     * 当接收到订单信息后就要进行下单操作了
     * @param message
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收消息："+message);
        SecKillMessage secKillMessage = JsonUtil.jsonToPojo(message, SecKillMessage.class);
        Long goodsId = secKillMessage.getGoodsId();
        User user = secKillMessage.getUser();

        //判断库存
        CommonGood commonGood = goodsService.findGoodsByGoodsId(goodsId);

        //对于库存一定要去数据库查，而不是使用前端传过来的数据
        if(commonGood.getStockCount()<1){
            return;
        }

        //判断是否重复进行抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return;
        }

        //下单
        orderService.seckill(user, commonGood);


    }
}
