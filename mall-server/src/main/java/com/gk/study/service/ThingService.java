package com.gk.study.service;

import com.gk.study.entity.Thing;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务接口
 *
 * 该接口定义了商品相关的所有业务操作。
 * 主要功能包括：
 * 1. 商品的创建、删除和更新
 * 2. 商品列表的获取和搜索
 * 3. 商品统计数据的更新
 * 4. 商品库存的管理
 *
 * 商品属性说明：
 * 1. 基本信息：名称、描述、价格等
 * 2. 分类信息：所属分类、标签等
 * 3. 统计数据：销量、收藏数、评分等
 * 4. 库存信息：库存数量、库存状态等
 *
 * 注意：
 * 1. 商品创建需要验证必填信息
 * 2. 商品删除需要检查关联订单
 * 3. 库存操作需要保证原子性
 * 4. 统计数据需要及时更新
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Thing
 */
public interface ThingService {
    
    /**
     * 获取商品列表
     *
     * 根据条件查询商品列表。
     * 支持按关键词、排序方式、分类和标签进行筛选。
     *
     * @param keyword 搜索关键词，可选
     * @param sort 排序方式，可选值：price（价格）、sales（销量）、score（评分）
     * @param c 分类ID，可选
     * @param tag 标签，可选
     * @return List<Thing> 符合条件的商品列表
     */
    List<Thing> getThingList(String keyword, String sort, String c, String tag);
    
    /**
     * 创建商品
     *
     * 创建新的商品记录。
     * 创建过程包括：
     * 1. 验证商品信息完整性
     * 2. 设置商品初始状态
     * 3. 保存商品信息
     *
     * @param thing 商品对象，包含商品详细信息
     * @throws IllegalArgumentException 当商品信息不完整时抛出
     */
    void createThing(Thing thing);
    
    /**
     * 删除商品
     *
     * 删除指定的商品记录。
     * 删除前需要检查：
     * 1. 商品是否存在
     * 2. 商品是否有关联订单
     * 3. 商品是否在售
     *
     * @param id 商品ID
     * @throws IllegalStateException 当商品不允许删除时抛出
     */
    void deleteThing(String id);

    /**
     * 更新商品
     *
     * 更新商品信息。
     * 更新内容包括：
     * 1. 商品基本信息
     * 2. 商品价格
     * 3. 商品状态
     *
     * @param thing 商品对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateThing(Thing thing);

    /**
     * 获取商品详情
     *
     * 查询指定商品的详细信息。
     * 包括商品基本信息、统计数据等。
     *
     * @param id 商品ID
     * @return Thing 商品详情对象
     * @throws IllegalArgumentException 当商品ID为空时抛出
     */
    Thing getThingById(String id);

    /**
     * 增加商品收藏数
     *
     * 当用户收藏商品时，增加商品的收藏数。
     * 收藏数增加1。
     *
     * @param thingId 商品ID
     * @throws IllegalArgumentException 当商品ID为空时抛出
     */
    void addWishCount(String thingId);

    /**
     * 增加商品收藏数
     *
     * 当用户收藏商品时，增加商品的收藏数。
     * 收藏数增加1。
     *
     * @param thingId 商品ID
     * @throws IllegalArgumentException 当商品ID为空时抛出
     */
    void addCollectCount(String thingId);
    
    /**
     * 增加商品销量
     *
     * 当订单完成时，增加商品的销量。
     * 销量增加指定数量。
     *
     * @param thingId 商品ID
     * @param count 增加的销量数量
     * @throws IllegalArgumentException 当参数无效时抛出
     */
    void addSalesCount(String thingId, Integer count);
    
    /**
     * 增加商品评分
     *
     * 当用户评价商品时，更新商品的评分。
     * 评分采用加权平均计算。
     *
     * @param thingId 商品ID
     * @param score 新增的评分
     * @throws IllegalArgumentException 当参数无效时抛出
     */
    void addScore(String thingId, BigDecimal score);
    
    /**
     * 更新商品浏览量
     *
     * 当用户查看商品详情时，增加商品的浏览量。
     * 浏览量增加1。
     *
     * @param thingId 商品ID
     * @throws IllegalArgumentException 当商品ID为空时抛出
     */
    void updateViewCount(String thingId);
    
    /**
     * 减少商品库存
     *
     * 当订单创建时，减少商品的库存数量。
     * 需要保证库存操作的原子性。
     *
     * @param thingId 商品ID
     * @param count 减少的库存数量
     * @return boolean 是否成功减少库存
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws IllegalStateException 当库存不足时抛出
     */
    boolean reduceRepertory(String thingId, Integer count);
}
