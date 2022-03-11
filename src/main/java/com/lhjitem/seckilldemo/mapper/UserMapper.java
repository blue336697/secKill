package com.lhjitem.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhjitem.seckilldemo.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author duoduo
 * @since 2021-10-26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
