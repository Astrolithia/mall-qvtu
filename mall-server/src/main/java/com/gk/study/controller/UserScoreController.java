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
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/userScore")
public class UserScoreController {

    private final static Logger logger = LoggerFactory.getLogger(UserScoreController.class);

    @Autowired
    private UserScoreService userScoreService;

    /**
     * 获取用户积分记录列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse getScoreList(String userId) throws IOException {
        List<UserScore> list = userScoreService.getUserScoreList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", list);
    }

    /**
     * 获取用户总积分
     */
    @RequestMapping(value = "/total", method = RequestMethod.GET)
    public APIResponse getTotalScore(String userId) throws IOException {
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", userScoreService.getUserTotalScore(userId));
    }
} 