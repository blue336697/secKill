package com.lhjitem.seckilldemo.controller;


import com.lhjitem.seckilldemo.domain.CommonBean;
import com.lhjitem.seckilldemo.domain.CommonOrder;
import com.lhjitem.seckilldemo.domain.CommonResult;
import com.lhjitem.seckilldemo.pojo.User;
import com.lhjitem.seckilldemo.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @GetMapping("/detail")
    @ResponseBody
    public CommonBean detail(User user, Long orderId){
        if(user == null)
            return CommonBean.error(CommonResult.LOGIN_EXCEPTION);
        CommonOrder commonOrder = orderService.detail(orderId);
        return CommonBean.success(commonOrder);
    }
}
