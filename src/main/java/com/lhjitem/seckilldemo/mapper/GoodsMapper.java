package com.lhjitem.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhjitem.seckilldemo.domain.CommonGood;
import com.lhjitem.seckilldemo.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<CommonGood> findGoods();

    /**
     * 获取商品详情
     * @return
     * @param goodsId
     */
    CommonGood findGoodsByGoodsId(Long goodsId);
}
