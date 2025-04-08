package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 *
 * 该接口定义了订单相关的所有业务操作。
 * 主要功能包括：
 * 1. 订单的创建、支付和删除
 * 2. 订单信息的查询和更新
 * 3. 用户订单列表的获取
 *
 * 订单状态说明：
 * 1. 待支付：订单创建后未支付
 * 2. 已支付：订单已完成支付
 * 3. 已取消：订单被取消
 * 4. 已完成：订单已完成所有流程
 *
 * 注意：
 * 1. 订单创建后需要生成唯一的订单号
 * 2. 支付操作需要保证事务性
 * 3. 订单删除需要检查状态
 * 4. 订单更新需要记录变更日志
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Order
 */
public interface OrderService {
    
    /**
     * 获取所有订单列表
     *
     * 查询系统中的所有订单记录。
     * 返回结果按创建时间倒序排列。
     *
     * @return List<Order> 订单列表，按创建时间倒序排列
     */
    List<Order> getOrderList();
    
    /**
     * 创建订单
     *
     * 创建新的订单记录。
     * 创建过程包括：
     * 1. 生成唯一订单号
     * 2. 设置订单初始状态
     * 3. 保存订单信息
     *
     * @param order 订单对象，包含订单详细信息
     * @throws IllegalArgumentException 当订单信息不完整时抛出
     */
    void createOrder(Order order);
    
    /**
     * 支付订单
     *
     * 处理订单支付操作。
     * 支付过程包括：
     * 1. 验证订单状态
     * 2. 处理支付信息
     * 3. 更新订单状态
     *
     * @param order 订单对象，包含支付信息
     * @throws IllegalStateException 当订单状态不允许支付时抛出
     */
    void payOrder(Order order);
    
    /**
     * 删除订单
     *
     * 删除指定的订单记录。
     * 删除前需要检查：
     * 1. 订单是否存在
     * 2. 订单状态是否允许删除
     *
     * @param id 订单ID
     * @throws IllegalStateException 当订单状态不允许删除时抛出
     */
    void deleteOrder(String id);

    /**
     * 更新订单
     *
     * 更新订单信息。
     * 更新内容包括：
     * 1. 订单基本信息
     * 2. 订单状态
     * 3. 订单备注
     *
     * @param order 订单对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateOrder(Order order);

    /**
     * 获取用户订单列表
     *
     * 查询指定用户的订单记录。
     * 可以根据订单状态进行筛选。
     *
     * @param userId 用户ID
     * @param status 订单状态，可选参数
     * @return List<Order> 用户订单列表
     * @throws IllegalArgumentException 当用户ID为空时抛出
     */
    List<Order> getUserOrderList(String userId, String status);
    
    /**
     * 根据订单号获取订单信息
     *
     * 通过订单号查询订单详细信息。
     *
     * @param orderNumber 订单号
     * @return Order 订单信息
     * @throws IllegalArgumentException 当订单号为空时抛出
     */
    Order getOrderById(String orderNumber);
    
    /**
     * 根据ID获取订单信息
     *
     * 通过订单ID查询订单详细信息。
     *
     * @param id 订单ID
     * @return Order 订单信息
     * @throws IllegalArgumentException 当订单ID为空时抛出
     */
    Order getById(Long id);
}
