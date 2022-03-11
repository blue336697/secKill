package com.lhjitem.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.pojo.Goods;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<CommonGood> findGoods();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    CommonGood findGoodsByGoodsId(Long goodsId);
}
