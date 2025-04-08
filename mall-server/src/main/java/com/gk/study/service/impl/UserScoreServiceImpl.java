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
 * @author Administrator
 * @date 2024-03-26
 */
@Service
public class UserScoreServiceImpl implements UserScoreService {

    @Autowired
    private UserScoreMapper userScoreMapper;

    @Override
    @Transactional
    public void addUserScore(UserScore userScore) {
        userScore.setCreateTime(System.currentTimeMillis());
        userScoreMapper.insert(userScore);
    }

    @Override
    public List<UserScore> getUserScoreList(String userId) {
        return userScoreMapper.getUserScoreList(userId);
    }

    @Override
    public BigDecimal getUserTotalScore(String userId) {
        return userScoreMapper.getUserTotalScore(userId);
    }

    @Override
    @Transactional
    public void addOrderConfirmScore(String userId, String orderId, BigDecimal amount, String thingId) {
        UserScore userScore = new UserScore();
        userScore.setUserId(userId);
        userScore.setOrderId(orderId);
        userScore.setThingId(thingId);
        // 确认收货获得订单金额的积分（1元=1积分）
        userScore.setScore(amount);
        userScore.setType("ORDER_CONFIRM");
        userScore.setDescription("确认收货获得积分");
        addUserScore(userScore);
    }

    @Override
    @Transactional
    public void addCommentScore(String userId, String thingId, String orderId) {
        UserScore userScore = new UserScore();
        userScore.setUserId(userId);
        userScore.setThingId(thingId);
        userScore.setOrderId(orderId);
        // 评价获得5积分
        userScore.setScore(new BigDecimal("5"));
        userScore.setType("COMMENT");
        userScore.setDescription("评价商品获得积分");
        addUserScore(userScore);
    }
} 