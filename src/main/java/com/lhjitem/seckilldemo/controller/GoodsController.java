package com.lhjitem.seckilldemo.controller;

import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonDetail;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IGoodsService;
import com.lhjitem.seckilldemo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author lhj
 * @create 2021/10/27 20:11
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    //这里进行QPS（）的优化，将频繁访问数据库的操作缓存在redis中
    private RedisTemplate redisTemplate;

    @Autowired
    //使用这个帮助我们手动渲染
    private ThymeleafViewResolver thymeleafViewResolver;
    /**
     * 跳转商品页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
                         @CookieValue("login_token") String ticket){
        //
        if (StringUtils.isEmpty(ticket))
            return "login";

        //根据cookie去获取登录的用户
//        User user = (User) session.getAttribute(ticket);

        User user = userService.getUserByCookie(ticket, request, response);
        if(user==null)
            return "login";

        //将页面缓存在redis中，自己处理渲染并且返回给web服务器
        //Redis获取页面如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //通过MVC中的Model传到前端
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoods());

        //如果为空则要手动渲染，并且存入redis并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        String goodsList = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if(!StringUtils.isEmpty(goodsList))
            //注意这里要设置过期时间，因为虽然要做页面静态化，但是其中的秒杀数据是要做到更新的
            valueOperations.set("goodsList", goodsList, 60, TimeUnit.SECONDS);
        return goodsList;
    }


    /**
     * 现在做url缓存，本质上还是页面缓存
     * @param request
     * @param response
     * @param model
     * @param ticket
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(HttpServletRequest request,HttpServletResponse response,Model model,@CookieValue("login_token") String ticket,@PathVariable Long goodsId){
        if (StringUtils.isEmpty(ticket))
            return "login";

        User user = userService.getUserByCookie(ticket, request, response);
        if(user==null)
            return "login";

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面，如果不为空，直接返回页才
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

            //通过MVC中的Model传到前端
        model.addAttribute("user",user);
        CommonGood commonGood = goodsService.findGoodsByGoodsId(goodsId);
        Date startDate = commonGood.getStartDate();
        Date endDate = commonGood.getEndDate();
        Date currentDate = new Date();
        int secKillStatus = 0;  //秒杀状态
        int remainSeconds = 0;  //秒杀倒计时
        //秒杀还没开始
        if(currentDate.before(startDate)){
            remainSeconds = ((int) ((startDate.getTime() - currentDate.getTime()) / 1000));
        }else if(currentDate.after(endDate)){   //秒杀已经结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {     //秒杀正在进行
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("goods",commonGood);

        //继续手动渲染
        //如果为空则要手动渲染，并且存入redis并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        String goodsList = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if(!StringUtils.isEmpty(goodsList))
            //注意这里要设置过期时间，因为虽然要做页面静态化，但是其中的秒杀数据是要做到更新的
            valueOperations.set("goodsDetail:"+goodsId, goodsList, 60, TimeUnit.SECONDS);
        return goodsList;
    }


    /**
     * 现在开始页面静态化，将不变更的页面内容直接缓存在浏览器中，减少数据的传输量，这也是前后端分离的形式，后端只需要调用接口将每次变更的
     * 值发送给前端显示即可
     * @param request
     * @param response
     * @param ticket
     * @param goodsId
     * @return
     */
    @RequestMapping("/toDetail/{goodsId}")
    @ResponseBody
    public CommonBean toDetail(HttpServletRequest request, HttpServletResponse response,@CookieValue("login_token") String ticket, @PathVariable Long goodsId){
//        if (StringUtils.isEmpty(ticket))
//            return "login";
        if(StringUtils.isEmpty(ticket))
            return CommonBean.error(CommonResult.LOGIN_EXCEPTION);

        User user = userService.getUserByCookie(ticket, request, response);
        if (user == null)
            return CommonBean.error(CommonResult.LOGIN_EXCEPTION);
//        if(user==null)
//            return "login";


        CommonGood commonGood = goodsService.findGoodsByGoodsId(goodsId);
        Date startDate = commonGood.getStartDate();
        Date endDate = commonGood.getEndDate();
        Date currentDate = new Date();
        int secKillStatus = 0;  //秒杀状态
        int remainSeconds = 0;  //秒杀倒计时
        //秒杀还没开始
        if(currentDate.before(startDate)){
            remainSeconds = ((int) ((startDate.getTime() - currentDate.getTime()) / 1000));
        }else if(currentDate.after(endDate)){   //秒杀已经结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {     //秒杀正在进行
            secKillStatus = 1;
            remainSeconds = 0;
        }
        CommonDetail commonDetail = new CommonDetail();
        commonDetail.setUser(user);
        commonDetail.setCommonGood(commonGood);
        commonDetail.setSecKillStatus(secKillStatus);
        commonDetail.setRemainSeconds(remainSeconds);
        return CommonBean.success(commonDetail);
    }
}
