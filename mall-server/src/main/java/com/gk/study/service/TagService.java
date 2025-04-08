package com.gk.study.service;

import com.gk.study.entity.Tag;
import java.util.List;

/**
 * 标签服务接口
 *
 * 该接口定义了商品标签相关的所有业务操作。
 * 主要功能包括：
 * 1. 标签的创建、删除和更新
 * 2. 标签列表的获取
 * 3. 商品标签的关联查询
 *
 * 标签属性说明：
 * 1. 标签名称：标签的唯一标识
 * 2. 标签描述：标签的详细说明
 * 3. 创建时间：标签的创建时间
 * 4. 使用次数：标签被使用的次数
 *
 * 注意：
 * 1. 标签名称必须唯一
 * 2. 删除标签需要检查是否有关联商品
 * 3. 标签更新需要同步更新关联商品
 * 4. 标签列表需要支持分页查询
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Tag
 */
public interface TagService {
    
    /**
     * 获取标签列表
     *
     * 查询系统中的所有标签。
     * 返回结果按使用次数倒序排列。
     *
     * @return List<Tag> 标签列表，按使用次数倒序排列
     */
    List<Tag> getTagList();
    
    /**
     * 创建标签
     *
     * 创建新的标签记录。
     * 创建过程包括：
     * 1. 验证标签名称唯一性
     * 2. 设置初始使用次数
     * 3. 保存标签信息
     *
     * @param tag 标签对象，包含标签详细信息
     * @throws IllegalArgumentException 当标签信息不完整时抛出
     * @throws IllegalStateException 当标签名称已存在时抛出
     */
    void createTag(Tag tag);
    
    /**
     * 删除标签
     *
     * 删除指定的标签记录。
     * 删除前需要检查：
     * 1. 标签是否存在
     * 2. 标签是否有关联商品
     *
     * @param id 标签ID
     * @throws IllegalArgumentException 当标签ID为空时抛出
     * @throws IllegalStateException 当标签不允许删除时抛出
     */
    void deleteTag(String id);

    /**
     * 更新标签
     *
     * 更新标签信息。
     * 更新内容包括：
     * 1. 标签名称
     * 2. 标签描述
     * 3. 使用次数
     *
     * @param tag 标签对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateTag(Tag tag);
    
    /**
     * 获取商品标签
     *
     * 查询指定商品的所有标签。
     * 用于展示商品的标签信息。
     *
     * @param thingId 商品ID
     * @return List<Tag> 商品关联的标签列表
     * @throws IllegalArgumentException 当商品ID为空时抛出
     */
    List<Tag> getTagsByThingId(String thingId);
}
