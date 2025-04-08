package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Order;
import com.gk.study.entity.Thing;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.OrderService;
import com.gk.study.service.ThingService;
import com.gk.study.service.UserScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService service;
    
    @Autowired
    ThingService thingService;
    
    @Autowired
    UserScoreService userScoreService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Order> list =  service.getOrderList();

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 用户订单
    @RequestMapping(value = "/userOrderList", method = RequestMethod.GET)
    public APIResponse userOrderList(String userId, String status){
        List<Order> list =  service.getUserOrderList(userId, status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Order order) throws IOException {
        service.createOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }
    
    @RequestMapping(value = "/payOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse payOrder(Order order) throws IOException {
        // 先获取订单信息，因为前端传来的可能只有订单号
        Order existingOrder = service.getOrderById(order.getOrderNumber());
        if (existingOrder == null) {
            logger.error("订单支付失败: 订单不存在 {}", order.getOrderNumber());
            return new APIResponse(ResponeCode.FAIL, "订单不存在");
        }
        
        // 获取商品ID和数量
        String thingId = existingOrder.getThingId();
        String countStr = existingOrder.getCount();
        
        if (thingId == null || countStr == null) {
            logger.error("订单商品信息不完整: thingId={}, count={}", thingId, countStr);
            return new APIResponse(ResponeCode.FAIL, "订单商品信息不完整");
        }
        
        try {
            Integer count = Integer.parseInt(countStr);
            
            // 检查库存并减少库存
            boolean success = thingService.reduceRepertory(thingId, count);
            if (!success) {
                logger.error("订单支付失败: 商品库存不足，商品ID={}, 需要数量={}", thingId, count);
                return new APIResponse(ResponeCode.FAIL, "商品库存不足，无法完成支付");
            }
            
            // 更新订单为已支付状态
            service.payOrder(order);
            
            // 增加商品销量
            thingService.addSalesCount(thingId, count);
            
            logger.info("订单支付成功: 商品ID={}, 数量={}, 订单号={}", 
                     thingId, count, order.getOrderNumber());
            
            return new APIResponse(ResponeCode.SUCCESS, "支付成功");
        } catch (NumberFormatException e) {
            logger.error("订单商品数量格式错误: {}", countStr, e);
            return new APIResponse(ResponeCode.FAIL, "订单数量格式错误");
        }
    }


    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteOrder(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Order order) throws IOException {
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public APIResponse cancelOrder(Long id) throws IOException {
        Order order = new Order();
        order.setId(id);
        order.setStatus("7"); // 7=取消
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelUserOrder(Long id) throws IOException {
        Order order = new Order();
        order.setId(id);
        order.setStatus("7"); // 7=取消
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }
    
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/ship", method = RequestMethod.POST)
    @Transactional
    public APIResponse ship(Order order) throws IOException {
        if (order.getId() == null) {
            return new APIResponse(ResponeCode.FAIL, "订单ID不能为空");
        }
        
        logger.info("订单发货请求参数: id={}, trackingNumber={}, shippingCompany={}", 
                   order.getId(), order.getTrackingNumber(), order.getShippingCompany());
        
        // 如果缺少物流信息则返回错误
        if (order.getTrackingNumber() == null || order.getTrackingNumber().trim().isEmpty()) {
            logger.warn("订单发货失败: 缺少快递单号");
            return new APIResponse(ResponeCode.FAIL, "请输入快递单号");
        }
        
        // 设置订单状态为待收货(3)
        order.setStatus("3");
        
        // 更新订单信息
        service.updateOrder(order);
        logger.info("订单发货成功，订单ID：{}，快递单号：{}", order.getId(), order.getTrackingNumber());
        
        return new APIResponse(ResponeCode.SUCCESS, "发货成功");
    }
    
    @RequestMapping(value = "/checkShipping", method = RequestMethod.GET)
    public APIResponse checkShipping(String trackingNumber, String orderId) {
        if (trackingNumber == null || trackingNumber.isEmpty()) {
            return new APIResponse(ResponeCode.FAIL, "快递单号不能为空");
        }
        
        // 这里可以调用物流API查询物流信息，目前返回模拟数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", "物流查询功能正在开发中");
    }

    @RequestMapping(value = "/confirmReceipt", method = RequestMethod.POST)
    @Transactional
    public APIResponse confirmReceipt(Long id) throws IOException {
        if (id == null) {
            return new APIResponse(ResponeCode.FAIL, "订单ID不能为空");
        }
        
        // 获取订单信息
        Order order = service.getById(id);
        if (order == null) {
            return new APIResponse(ResponeCode.FAIL, "订单不存在");
        }
        
        // 检查订单状态是否为待收货
        if (!"3".equals(order.getStatus())) {
            return new APIResponse(ResponeCode.FAIL, "只有待收货的订单可以确认收货");
        }
        
        // 更新订单状态为待评价(4)
        order.setStatus("4");
        service.updateOrder(order);
        
        // 获取商品信息并计算积分
        Thing thing = thingService.getThingById(order.getThingId());
        if (thing != null && thing.getPrice() != null) {
            try {
                // 计算总价：单价 * 数量
                BigDecimal unitPrice = thing.getPrice();
                int quantity = Integer.parseInt(order.getCount());
                BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));
                
                userScoreService.addOrderConfirmScore(order.getUserId(), String.valueOf(order.getId()), totalAmount, order.getThingId());
            } catch (NumberFormatException e) {
                logger.error("计算订单积分失败: 订单ID={}, 商品ID={}, 单价={}, 数量={}", 
                    order.getId(), order.getThingId(), thing.getPrice(), order.getCount(), e);
                // 积分添加失败不影响确认收货
            }
        }
        
        return new APIResponse(ResponeCode.SUCCESS, "确认收货成功");
    }
}
