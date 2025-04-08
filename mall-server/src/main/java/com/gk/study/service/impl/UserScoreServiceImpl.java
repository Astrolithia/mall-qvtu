package com.gk.study.service.impl;

import com.gk.study.entity.UserScore;
import com.gk.study.mapper.UserScoreMapper;
import com.gk.study.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户积分服务实现类
 * 
 * 该类实现了用户积分相关的业务逻辑，包括：
 * 1. 用户积分记录的创建
 * 2. 用户积分列表的获取
 * 3. 用户总积分的获取
 * 4. 基于不同行为的积分奖励（确认订单、评价商品等）
 * 
 * 使用事务注解确保积分操作的原子性和一致性。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class UserScoreServiceImpl implements UserScoreService {

    /**
     * 用户积分数据访问对象
     * 用于执行用户积分相关的数据库操作
     */
    @Autowired
    private UserScoreMapper userScoreMapper;

    /**
     * 添加用户积分记录
     * 
     * 创建新的用户积分记录，自动设置创建时间。
     * 该方法使用事务注解确保操作的原子性。
     * 
     * @param userScore 要创建的用户积分对象
     */
    @Override
    @Transactional
    public void addUserScore(UserScore userScore) {
        // 设置创建时间为当前时间戳
        userScore.setCreateTime(System.currentTimeMillis());
        // 插入积分记录
        userScoreMapper.insert(userScore);
    }

    /**
     * 获取用户积分记录列表
     * 
     * 查询指定用户的所有积分记录。
     * 返回的列表包含积分来源、数量、时间等信息。
     * 
     * @param userId 用户ID
     * @return List<UserScore> 用户积分记录列表
     */
    @Override
    public List<UserScore> getUserScoreList(String userId) {
        return userScoreMapper.getUserScoreList(userId);
    }

    /**
     * 获取用户总积分
     * 
     * 计算指定用户的总积分数量。
     * 通过累加所有积分记录得到最终结果。
     * 
     * @param userId 用户ID
     * @return BigDecimal 用户总积分
     */
    @Override
    public BigDecimal getUserTotalScore(String userId) {
        return userScoreMapper.getUserTotalScore(userId);
    }

    /**
     * 添加订单确认积分
     * 
     * 用户确认收货后，根据订单金额奖励相应积分。
     * 订单金额与积分比例为1:1（1元=1积分）。
     * 该方法使用事务注解确保操作的原子性。
     * 
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param amount 订单金额
     * @param thingId 商品ID
     */
    @Override
    @Transactional
    public void addOrderConfirmScore(String userId, String orderId, BigDecimal amount, String thingId) {
        // 创建积分记录对象
        UserScore userScore = new UserScore();
        // 设置用户ID
        userScore.setUserId(userId);
        // 设置订单ID
        userScore.setOrderId(orderId);
        // 设置商品ID
        userScore.setThingId(thingId);
        // 确认收货获得订单金额的积分（1元=1积分）
        userScore.setScore(amount);
        // 设置积分类型
        userScore.setType("ORDER_CONFIRM");
        // 设置积分描述
        userScore.setDescription("确认收货获得积分");
        // 添加积分记录
        addUserScore(userScore);
    }

    /**
     * 添加评价积分
     * 
     * 用户评价商品后，奖励固定积分（5分）。
     * 该方法使用事务注解确保操作的原子性。
     * 
     * @param userId 用户ID
     * @param thingId 商品ID
     * @param orderId 订单ID
     */
    @Override
    @Transactional
    public void addCommentScore(String userId, String thingId, String orderId) {
        // 创建积分记录对象
        UserScore userScore = new UserScore();
        // 设置用户ID
        userScore.setUserId(userId);
        // 设置商品ID
        userScore.setThingId(thingId);
        // 设置订单ID
        userScore.setOrderId(orderId);
        // 评价获得5积分
        userScore.setScore(new BigDecimal("5"));
        // 设置积分类型
        userScore.setType("COMMENT");
        // 设置积分描述
        userScore.setDescription("评价商品获得积分");
        // 添加积分记录
        addUserScore(userScore);
    }
} 