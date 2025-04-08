package com.gk.study.service;

import com.gk.study.entity.UserScore;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户积分服务接口
 * @author Administrator
 * @date 2024-03-26
 */
public interface UserScoreService {
    
    /**
     * 添加积分记录
     * @param userScore 积分记录
     */
    void addUserScore(UserScore userScore);
    
    /**
     * 获取用户积分记录列表
     * @param userId 用户ID
     * @return 积分记录列表
     */
    List<UserScore> getUserScoreList(String userId);
    
    /**
     * 获取用户总积分
     * @param userId 用户ID
     * @return 总积分
     */
    BigDecimal getUserTotalScore(String userId);
    
    /**
     * 确认收货增加积分
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param amount 订单金额
     * @param thingId 商品ID
     */
    void addOrderConfirmScore(String userId, String orderId, BigDecimal amount, String thingId);
    
    /**
     * 评价增加积分
     * @param userId 用户ID
     * @param thingId 商品ID
     * @param orderId 订单ID
     */
    void addCommentScore(String userId, String thingId, String orderId);
} 