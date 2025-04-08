package com.gk.study.service;

import com.gk.study.entity.ThingWish;
import java.util.List;
import java.util.Map;

/**
 * 商品收藏服务接口
 *
 * 该接口定义了商品收藏相关的所有业务操作。
 * 主要功能包括：
 * 1. 商品收藏的创建和删除
 * 2. 用户收藏列表的获取
 * 3. 收藏状态的查询
 *
 * 商品收藏属性说明：
 * 1. 用户ID：收藏商品的用户
 * 2. 商品ID：被收藏的商品
 * 3. 收藏时间：用户收藏的时间
 * 4. 收藏状态：是否有效
 *
 * 注意：
 * 1. 用户不能重复收藏同一商品
 * 2. 收藏操作需要更新商品的收藏数
 * 3. 取消收藏需要检查收藏记录是否存在
 * 4. 收藏列表需要支持分页查询
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see ThingWish
 */
public interface ThingWishService {
    
    /**
     * 获取用户收藏列表
     *
     * 查询指定用户的所有收藏商品。
     * 返回结果包含商品详细信息。
     *
     * @param userId 用户ID
     * @return List<Map> 收藏商品列表，每个商品包含完整信息
     * @throws IllegalArgumentException 当用户ID为空时抛出
     */
    List<Map> getThingWishList(String userId);
    
    /**
     * 创建商品收藏
     *
     * 创建新的商品收藏记录。
     * 创建过程包括：
     * 1. 检查是否已收藏
     * 2. 创建收藏记录
     * 3. 更新商品收藏数
     *
     * @param thingWish 收藏对象，包含用户ID和商品ID
     * @throws IllegalArgumentException 当收藏信息不完整时抛出
     * @throws IllegalStateException 当商品已收藏时抛出
     */
    void createThingWish(ThingWish thingWish);
    
    /**
     * 删除商品收藏
     *
     * 删除指定的收藏记录。
     * 删除过程包括：
     * 1. 检查收藏记录是否存在
     * 2. 删除收藏记录
     * 3. 更新商品收藏数
     *
     * @param id 收藏记录ID
     * @throws IllegalArgumentException 当收藏ID为空时抛出
     * @throws IllegalStateException 当收藏记录不存在时抛出
     */
    void deleteThingWish(String id);

    /**
     * 获取收藏状态
     *
     * 查询指定用户对指定商品的收藏状态。
     * 用于判断用户是否已收藏某商品。
     *
     * @param userId 用户ID
     * @param thingId 商品ID
     * @return ThingWish 收藏记录，如果未收藏则返回null
     * @throws IllegalArgumentException 当用户ID或商品ID为空时抛出
     */
    ThingWish getThingWish(String userId, String thingId);
}
