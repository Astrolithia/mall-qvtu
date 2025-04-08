package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.UserScore;
import com.gk.study.service.UserScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


/**
 * 用户积分控制器
 *
 * 该控制器负责处理与用户积分相关的所有HTTP请求，包括查询用户积分记录和
 * 获取用户总积分等功能。积分系统用于跟踪用户在平台上的活动并提供奖励机制。
 * 
 * 积分系统的主要功能：
 * 1. 查询用户积分记录列表
 * 2. 查询用户当前总积分
 * 
 * 积分记录包含来源、数量、时间等信息，用于追踪用户积分变动历史。
 * 
 * @author 系统生成
 * @version 1.0
 */
@RestController
@RequestMapping("/userScore")
public class UserScoreController {

    /**
     * 日志记录器
     * 用于记录控制器操作和异常信息，便于系统监控和问题排查
     */
    private final static Logger logger = LoggerFactory.getLogger(UserScoreController.class);

    /**
     * 用户积分服务
     * 处理与用户积分相关的业务逻辑，包括积分记录查询和积分计算等
     */
    @Autowired
    private UserScoreService userScoreService;

    /**
     * 获取用户积分记录列表
     *
     * 该方法返回指定用户的所有积分记录，包括积分获取和消费的历史记录。
     * 用户可以通过此接口查看自己的积分明细，了解积分来源和使用情况。
     * 记录按时间倒序排列，展示最新的积分变动。
     *
     * @param userId 用户ID，用于识别查询哪个用户的积分记录
     * @return APIResponse 包含状态码、提示信息和用户积分记录列表数据
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse getScoreList(String userId) throws IOException {
        // 记录开始查询日志
        logger.info("开始查询用户积分记录, userId={}", userId);
        
        // 调用服务层获取用户积分记录列表
        List<UserScore> list = userScoreService.getUserScoreList(userId);
        
        // 记录查询结果日志
        logger.info("查询用户积分记录完成, userId={}, 记录数量={}", userId, list.size());
        
        // 返回成功响应，包含积分记录列表
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 获取用户总积分
     *
     * 该方法返回指定用户的当前可用总积分数量，用于显示用户的积分余额。
     * 总积分是用户所有积分记录的净值，反映了用户当前可用的积分数量。
     * 此接口常用于用户中心页面显示积分余额，或在需要检查积分是否足够时调用。
     *
     * @param userId 用户ID，用于识别查询哪个用户的总积分
     * @return APIResponse 包含状态码、提示信息和用户总积分数据
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @RequestMapping(value = "/total", method = RequestMethod.GET)
    public APIResponse getTotalScore(String userId) throws IOException {
        // 记录开始查询日志
        logger.info("开始查询用户总积分, userId={}", userId);
        
        // 调用服务层获取用户总积分
        Object totalScore = userScoreService.getUserTotalScore(userId);
        
        // 记录查询结果日志
        logger.info("查询用户总积分完成, userId={}, 总积分={}", userId, totalScore);
        
        // 返回成功响应，包含总积分数值
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", totalScore);
    }
}