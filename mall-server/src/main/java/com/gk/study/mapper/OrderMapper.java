package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getList();

    List<Order> getUserOrderList(String userId, String status);
}
