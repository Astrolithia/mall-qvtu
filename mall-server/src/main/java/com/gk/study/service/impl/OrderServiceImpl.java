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
 * 订单服务实现类
 * 
 * 该类实现了订单相关的业务逻辑，包括：
 * 1. 订单列表的获取（全部订单和用户订单）
 * 2. 订单的创建、支付、删除和更新
 * 3. 订单状态的管理和转换
 * 4. 订单信息的查询
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 订单状态说明：
 * 1: 待支付
 * 2: 已支付
 * 3: 已发货
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    /**
     * 订单数据访问对象
     * 用于执行订单相关的数据库操作
     */
    @Autowired
    OrderMapper mapper;

    /**
     * 获取订单列表
     * 
     * 查询所有订单记录，通过自定义SQL实现。
     * 返回的列表包含所有订单的详细信息。
     * 
     * @return List<Order> 订单列表
     */
    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    /**
     * 创建订单
     * 
     * 创建新的订单记录，自动设置订单时间和状态。
     * 订单创建时默认状态为"1"（待支付）。
     * 
     * @param order 要创建的订单对象
     */
    @Override
    public void createOrder(Order order) {
        // 获取当前时间戳
        long ct = System.currentTimeMillis();
        // 设置订单时间
        order.setOrderTime(String.valueOf(ct));
        // 设置订单状态为待支付
        order.setStatus("1");
        // 插入订单记录
        mapper.insert(order);
    }

    /**
     * 支付订单
     * 
     * 更新订单状态为已支付（状态码"2"）。
     * 使用订单号作为更新条件。
     * 
     * @param order 包含订单号的订单对象
     */
    @Override
    public void payOrder(Order order) {
        // 创建更新条件
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        // 设置订单号条件
        updateWrapper.eq("order_number", order.getOrderNumber());
        // 设置订单状态为已支付
        order.setStatus("2");
        // 执行更新
        mapper.update(order, updateWrapper);
    }

    /**
     * 删除订单
     * 
     * 根据订单ID删除指定的订单记录。
     * 注意：删除操作不可恢复，请谨慎操作。
     * 
     * @param id 要删除的订单ID
     */
    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新订单
     * 
     * 更新指定订单的信息。
     * 如果订单状态更新为"3"（已发货），自动记录发货时间。
     * 
     * @param order 包含更新信息的订单对象
     */
    @Override
    public void updateOrder(Order order) {
        // 检查是否为发货操作
        if (order.getStatus() != null && order.getStatus().equals("3")) {
            // 设置发货时间
            order.setShippingTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        // 执行更新
        mapper.updateById(order);
    }

    /**
     * 获取用户订单列表
     * 
     * 根据用户ID和订单状态查询订单记录。
     * 通过自定义SQL实现，支持按状态筛选。
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return List<Order> 用户订单列表
     */
    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return mapper.getUserOrderList(userId, status);
    }

    /**
     * 根据订单号获取订单信息
     * 
     * 通过订单号查询订单的详细信息。
     * 使用订单号作为查询条件。
     * 
     * @param orderNumber 订单号
     * @return Order 订单信息
     */
    @Override
    public Order getOrderById(String orderNumber) {
        // 创建查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        // 设置订单号条件
        queryWrapper.eq("order_number", orderNumber);
        // 执行查询
        return mapper.selectOne(queryWrapper);
    }

    /**
     * 根据ID获取订单信息
     * 
     * 通过订单ID查询订单的详细信息。
     * 
     * @param id 订单ID
     * @return Order 订单信息
     */
    @Override
    public Order getById(Long id) {
        return mapper.selectById(id);
    }
}
