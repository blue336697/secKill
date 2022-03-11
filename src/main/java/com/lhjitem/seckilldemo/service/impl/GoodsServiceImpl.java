package com.lhjitem.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.mapper.GoodsMapper;
import com.lhjitem.seckilldemo.pojo.Goods;
import com.lhjitem.seckilldemo.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取商品列表
     * @return
     */
    @Override
    public List<CommonGood> findGoods() {
        return goodsMapper.findGoods();
    }

    @Override
    public CommonGood findGoodsByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsByGoodsId(goodsId);
    }
}
