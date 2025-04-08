package com.gk.study.service;

import com.gk.study.entity.UserScore;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户积分服务接口
 *
 * 该接口定义了用户积分相关的所有业务操作。
 * 主要功能包括：
 * 1. 积分记录的添加和查询
 * 2. 用户总积分的计算
 * 3. 订单确认后的积分奖励
 * 4. 商品评价后的积分奖励
 *
 * 积分规则：
 * 1. 订单确认：根据订单金额计算积分
 * 2. 商品评价：固定积分奖励
 * 3. 积分记录永久保存
 *
 * 注意：
 * 1. 所有积分操作都需要记录详细日志
 * 2. 积分计算需要考虑并发情况
 * 3. 积分记录需要包含操作类型和来源
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see UserScore
 */
public interface UserScoreService {
    
    /**
     * 添加积分记录
     *
     * 将用户的积分变动记录保存到数据库中。
     * 记录内容包括：
     * 1. 用户ID
     * 2. 积分变动值
     * 3. 变动类型
     * 4. 变动来源
     * 5. 变动时间
     *
     * @param userScore 积分记录对象，包含所有必要的积分变动信息
     * @throws IllegalArgumentException 当积分记录信息不完整时抛出
     */
    void addUserScore(UserScore userScore);
    
    /**
     * 获取用户积分记录列表
     *
     * 查询指定用户的所有积分变动记录。
     * 返回结果按时间倒序排列，最新的记录在前。
     *
     * @param userId 用户ID，用于查询该用户的积分记录
     * @return List<UserScore> 积分记录列表，按时间倒序排列
     * @throws IllegalArgumentException 当用户ID为空时抛出
     */
    List<UserScore> getUserScoreList(String userId);
    
    /**
     * 获取用户总积分
     *
     * 计算指定用户当前的总积分。
     * 总积分 = 所有积分记录的总和
     *
     * @param userId 用户ID，用于计算该用户的总积分
     * @return BigDecimal 用户当前的总积分
     * @throws IllegalArgumentException 当用户ID为空时抛出
     */
    BigDecimal getUserTotalScore(String userId);
    
    /**
     * 确认收货增加积分
     *
     * 当用户确认收货后，根据订单金额计算并添加积分。
     * 积分计算规则：
     * 1. 基础积分 = 订单金额 * 积分比例
     * 2. 特殊商品可能有额外积分奖励
     *
     * @param userId 用户ID，积分接收者
     * @param orderId 订单ID，用于记录积分来源
     * @param amount 订单金额，用于计算积分
     * @param thingId 商品ID，用于判断是否有额外积分奖励
     * @throws IllegalArgumentException 当参数无效时抛出
     */
    void addOrderConfirmScore(String userId, String orderId, BigDecimal amount, String thingId);
    
    /**
     * 评价增加积分
     *
     * 当用户完成商品评价后，添加固定积分奖励。
     * 积分奖励规则：
     * 1. 每个评价固定奖励一定积分
     * 2. 同一订单的同一商品只能评价一次
     *
     * @param userId 用户ID，积分接收者
     * @param thingId 商品ID，被评价的商品
     * @param orderId 订单ID，用于防止重复评价
     * @throws IllegalArgumentException 当参数无效时抛出
     */
    void addCommentScore(String userId, String thingId, String orderId);
} 