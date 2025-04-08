package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 * @author Administrator
 * @date 2024-03-26
 */
public interface OrderService {
    List<Order> getOrderList();
    void createOrder(Order order);
    void payOrder(Order order);
    void deleteOrder(String id);

    void updateOrder(Order order);

    List<Order> getUserOrderList(String userId, String status);
    
    /**
     * 根据订单号获取订单信息
     * @param orderNumber 订单号
     * @return 订单信息
     */
    Order getOrderById(String orderNumber);
    
    /**
     * 根据ID获取订单信息
     * @param id 订单ID
     * @return 订单信息
     */
    Order getById(Long id);
}
