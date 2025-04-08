package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Order;
import com.gk.study.service.OrderService;
import com.gk.study.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;

    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    @Override
    public void createOrder(Order order) {
        long ct = System.currentTimeMillis();
        order.setOrderTime(String.valueOf(ct));
        //order.setOrderNumber(String.valueOf(ct));
        order.setStatus("1");
        mapper.insert(order);
    }

    @Override
    public void payOrder(Order order) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_number",order.getOrderNumber());
        order.setStatus("2");
        mapper.update(order,updateWrapper);
    }
    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateOrder(Order order) {
        // 如果是发货操作，记录发货时间
        if (order.getStatus() != null && order.getStatus().equals("3")) {
            order.setShippingTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        mapper.updateById(order);
    }

    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return mapper.getUserOrderList(userId, status);
    }

    /**
     * 根据订单号获取订单信息
     * @param orderNumber 订单号
     * @return 订单信息
     */
    @Override
    public Order getOrderById(String orderNumber) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_number", orderNumber);
        return mapper.selectOne(queryWrapper);
    }

    @Override
    public Order getById(Long id) {
        return mapper.selectById(id);
    }
}
