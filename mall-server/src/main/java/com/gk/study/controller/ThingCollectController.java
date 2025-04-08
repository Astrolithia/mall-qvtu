package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ThingCollect;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingCollectService;
import com.gk.study.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商品收藏控制器
 * 提供商品收藏相关的功能接口
 * 主要功能包括：
 * 1. 收藏商品
 * 2. 取消收藏
 * 3. 获取用户收藏列表
 * 
 * 注意：收藏和取消收藏操作需要用户登录权限
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/thingCollect")
public class ThingCollectController {

    /**
     * 日志记录器，用于记录系统运行日志
     */
    private final static Logger logger = LoggerFactory.getLogger(ThingCollectController.class);

    /**
     * 商品收藏服务接口，用于处理收藏相关的业务逻辑
     */
    @Autowired
    ThingCollectService thingCollectService;

    /**
     * 商品服务接口，用于处理商品相关的业务逻辑
     */
    @Autowired
    ThingService thingService;

    /**
     * 收藏商品
     * 需要用户登录权限
     * 使用事务注解确保数据一致性
     * 如果用户已经收藏过该商品，则返回提示信息
     * 否则创建收藏记录并更新商品的收藏数
     * 
     * @param thingCollect 收藏记录对象，包含用户ID和商品ID
     * @return APIResponse 操作结果响应
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @Transactional
    public APIResponse collect(ThingCollect thingCollect) throws IOException {
        // 检查用户是否已经收藏过该商品
        if(thingCollectService.getThingCollect(thingCollect.getUserId(), thingCollect.getThingId()) != null){
            // 如果已收藏，返回提示信息
            return new APIResponse(ResponeCode.SUCCESS, "您已收藏过了");
        }else {
            // 创建收藏记录
            thingCollectService.createThingCollect(thingCollect);
            // 更新商品的收藏数
            thingService.addCollectCount(thingCollect.getThingId());
        }
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "收藏成功");
    }

    /**
     * 取消收藏
     * 需要用户登录权限
     * 使用事务注解确保数据一致性
     * 
     * @param id 收藏记录ID
     * @return APIResponse 操作结果响应
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unCollect", method = RequestMethod.POST)
    @Transactional
    public APIResponse unCollect(String id) throws IOException {
        // 删除收藏记录
        thingCollectService.deleteThingCollect(id);
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    /**
     * 获取用户的收藏列表
     * 不需要权限验证，所有用户都可以访问
     * 使用事务注解确保数据一致性
     * 
     * @param userId 用户ID
     * @return APIResponse 包含收藏列表的响应对象
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @RequestMapping(value = "/getUserCollectList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserCollectList(String userId) throws IOException {
        // 获取用户的收藏列表
        List<Map> lists = thingCollectService.getThingCollectList(userId);
        // 返回成功响应，包含收藏列表数据
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}
