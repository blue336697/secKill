package com.lhjitem.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhjitem.seckilldemo.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author duoduo
 * @since 2021-11-04
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
