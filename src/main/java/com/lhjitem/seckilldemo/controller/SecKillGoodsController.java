package com.lhjitem.seckilldemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lhjitem.seckilldemo.config.AccessLimit;
import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.exception.GlobalException;
import com.lhjitem.seckilldemo.pojo.Order;
import com.lhjitem.seckilldemo.pojo.SecKillMessage;
import com.lhjitem.seckilldemo.pojo.SeckillOrder;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.rabbitmq.MQSender;
import com.lhjitem.seckilldemo.service.IGoodsService;
import com.lhjitem.seckilldemo.service.IOrderService;
import com.lhjitem.seckilldemo.service.ISeckillOrderService;
import com.lhjitem.seckilldemo.service.IUserService;
import com.lhjitem.seckilldemo.utils.JsonUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  秒杀
 * @author duoduo
 * @since 2021-11-05
 */
@Slf4j
@Controller
@RequestMapping("/secKill")
public class SecKillGoodsController implements InitializingBean {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript redisScript;


    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /*@RequestMapping("/doSecKill2")
    public String doSecKill2(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("login_token") String ticket,Long goodsId){
        User user = userService.getUserByCookie(ticket, request, response);
        if(StringUtils.isEmpty(ticket)||user==null)
            return "login";
        model.addAttribute("user",user);
        CommonGood commonGood = goodsService.findGoodsByGoodsId(goodsId);

        //对于库存一定要去数据库查，而不是使用前端传过来的数据
        if(commonGood.getStockCount()<1){
            model.addAttribute("errmsg", CommonResult.Stock_Error.getMessage());
            return "secKillFail";
        }

        //判断订单是否有用户重新抢购，就看用户ID和商品ID 如果有重复出现就证明此用户重复抢购了
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));

        if(seckillOrder!=null){
            model.addAttribute("errmsg", CommonResult.Order_Error.getMessage());
            return "secKillFail";
        }

        Order order = orderService.seckill(user,commonGood);
        model.addAttribute("order",order);
        model.addAttribute("goods",commonGood);
        return "orderDetail";
    }*/


    /**
     * 秒杀页面静态化对应的controller改造
     * @param request
     * @param response
     * @param ticket
     * @param goodsId
     * @return
     */
    @PostMapping("/{path}/doSecKill")
    @ResponseBody
    public CommonBean doSecKill(HttpServletRequest request, HttpServletResponse response, @CookieValue("login_token") String ticket,Long goodsId, @PathVariable String path){
        User user = userService.getUserByCookie(ticket, request, response);
        if(StringUtils.isEmpty(ticket) || user==null)
            return CommonBean.error(CommonResult.LOGIN_EXCEPTION);

        ValueOperations valueOperations = redisTemplate.opsForValue();

        boolean check = orderService.checkPath(user, goodsId, path);
        if(!check)
            return CommonBean.error(CommonResult.REQUEST_ILLEGAL);

        //通过redis去获取seckillOrder，判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return CommonBean.error(CommonResult.Order_Error);
        }

        //通过内存标记，减少redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return CommonBean.error(CommonResult.Stock_Error);
        }

        //对库存进行预减，并且保证其原子性，拿到预减之后的库存
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);

        //现在在使用lua脚本来实现锁，使高并发场景下的数据一致性
        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if(stock < 0) {
            //这里为true就证明库存是空的
            EmptyStockMap.put(goodsId,true);
            //这是为了让redis的库存好看一点，因为当0个时还会再进来减一次那么这时再加回去
            valueOperations.increment("seckillGoods:" + goodsId);
            return CommonBean.error(CommonResult.Stock_Error);
        }



        //下订单
        SecKillMessage secKillMessage = new SecKillMessage(user, goodsId);
        mqSender.send(JsonUtil.objectToJson(secKillMessage));
        return CommonBean.success(0);


        /*
        //当在高并发的场景下，可能一个人发送了两个请求，这个两个请求可能很巧合的就被几乎无差别的处理了，
        //导致一个人买了两件秒杀商品，我们可以使用索引将用户id和商品id绑定，这样生成订单的时候会检查唯一性
        CommonGood commonGood = goodsService.findGoodsByGoodsId(goodsId);

        //对于库存一定要去数据库查，而不是使用前端传过来的数据
        if(commonGood.getStockCount()<1){
            return CommonBean.error(CommonResult.Stock_Error);
        }

        //判断订单是否有用户重新抢购，就看用户ID和商品ID 如果有重复出现就证明此用户重复抢购了
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));

        //通过redis去获取seckillOrder
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return CommonBean.error(CommonResult.Order_Error);
        }

        Order order = orderService.seckill(user,commonGood);
        return CommonBean.success(order);*/


    }


    /**
     * 获取秒杀结果，如果有orderId就成功，-1失败,0：排队中
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/getResult")
    @ResponseBody
    public CommonBean getResult(User user, Long goodsId){
        if(user == null)
            return CommonBean.error(CommonResult.LOGIN_EXCEPTION);
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return CommonBean.success(orderId);
    }


    /**
     * 这个方法就是接口安全优化，给真实的秒杀地址再套一层每个用户都独一无二的地址
     * @return
     */
    @AccessLimit(second = 5,maxCount = 5, needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public CommonBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user == null)
            return CommonBean.error(CommonResult.LOGIN_EXCEPTION);
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);

        //使用计数器法来限流，5秒内访问5次，下面这段被注解代替
        /*ValueOperations valueOperations = redisTemplate.opsForValue();
        String uri = request.getRequestURI();
        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if(count == null)
            valueOperations.set(uri + ":" + user.getId(),1,5,TimeUnit.SECONDS);
        else if(count < 5)
            valueOperations.increment(uri + ":" + user.getId());
        else
            return CommonBean.error(CommonResult.ACCESS_LIMIT);*/
        if(!check)
            return CommonBean.error(CommonResult.CAPTCHA_ILLEGAL);
        String str = orderService.createPath(user, goodsId);
        return CommonBean.success(str);
    }


    @RequestMapping(value = "/captcha" , method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user==null || goodsId<0){
            throw new GlobalException(CommonResult.REQUEST_ILLEGAL);
        }

        //设置请求头为输出图片的类型
        response.setContentType( "image/jpg");
        //这两个都设置成nocache是因为验证码是要每次都改变的
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader( "Expires", 0);
        //生成验证码，将结果放入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+" : "+goodsId ,captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response. getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }



            /**
             * 系统初始化，把商品库存数量加载到redis
             * @throws Exception
             */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<CommonGood> goods = goodsService.findGoods();
        if(CollectionUtils.isEmpty(goods))
            return;
        goods.forEach(commonGood -> {
            redisTemplate.opsForValue().set("seckillGoods:"+commonGood.getId(),commonGood.getStockCount());
            //这里建立一个map来进行标记redis里面的库存数量，如果大量的并发进来，redis的库存早已为0，
            // 但是还是会疯狂的进行减库存，这种频繁的通信也是很消耗资源的
            EmptyStockMap.put(commonGood.getId(), false);
        });

    }
}
