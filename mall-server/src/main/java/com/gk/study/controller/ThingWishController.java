package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ThingWish;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingService;
import com.gk.study.service.ThingWishService;
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
 * 商品心愿单控制器
 * 提供商品心愿单相关的功能接口
 * 主要功能包括：
 * 1. 添加商品到心愿单
 * 2. 从心愿单移除商品
 * 3. 获取用户的心愿单列表
 * 
 * 注意：添加和移除操作需要用户登录权限
 */
@RestController
@RequestMapping("/thingWish")
public class ThingWishController {

    /**
     * 日志记录器，用于记录系统运行日志
     */
    private final static Logger logger = LoggerFactory.getLogger(ThingWishController.class);

    /**
     * 商品心愿单服务接口，用于处理心愿单相关的业务逻辑
     */
    @Autowired
    ThingWishService thingWishService;

    /**
     * 商品服务接口，用于处理商品相关的业务逻辑
     */
    @Autowired
    ThingService thingService;

    /**
     * 添加商品到心愿单
     * 需要用户登录权限
     * 使用事务注解确保数据一致性
     * 如果用户已经添加过该商品，则返回提示信息
     * 否则创建心愿单记录并更新商品的收藏数
     * 
     * @param thingWish 心愿单记录对象，包含用户ID和商品ID
     * @return APIResponse 操作结果响应
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/wish", method = RequestMethod.POST)
    @Transactional
    public APIResponse wish(ThingWish thingWish) throws IOException {
        // 检查用户是否已经添加过该商品到心愿单
        if(thingWishService.getThingWish(thingWish.getUserId(), thingWish.getThingId()) != null){
            // 如果已添加，返回提示信息
            return new APIResponse(ResponeCode.SUCCESS, "您已添加过了");
        }else {
            // 创建心愿单记录
            thingWishService.createThingWish(thingWish);
            // 更新商品的收藏数
            thingService.addWishCount(thingWish.getThingId());
        }
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "添加成功");
    }

    /**
     * 从心愿单移除商品
     * 需要用户登录权限
     * 使用事务注解确保数据一致性
     * 
     * @param id 心愿单记录ID
     * @return APIResponse 操作结果响应
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unWish", method = RequestMethod.POST)
    @Transactional
    public APIResponse unWish(String id) throws IOException {
        // 删除心愿单记录
        thingWishService.deleteThingWish(id);
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    /**
     * 获取用户的心愿单列表
     * 不需要权限验证，所有用户都可以访问
     * 使用事务注解确保数据一致性
     * 
     * @param userId 用户ID
     * @return APIResponse 包含心愿单列表的响应对象
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @RequestMapping(value = "/getUserWishList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserWishList(String userId) throws IOException {
        // 获取用户的心愿单列表
        List<Map> lists = thingWishService.getThingWishList(userId);
        // 返回成功响应，包含心愿单列表数据
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}
