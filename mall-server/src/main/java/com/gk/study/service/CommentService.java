package com.gk.study.service;

import com.gk.study.entity.Comment;
import java.util.List;

/**
 * 评论服务接口
 *
 * 该接口定义了商品评论相关的所有业务操作。
 * 主要功能包括：
 * 1. 评论的创建、删除和更新
 * 2. 评论列表的获取（全部、商品、用户）
 * 3. 评论详情的获取
 *
 * 评论属性说明：
 * 1. 评论内容
 * 2. 评分等级
 * 3. 评论图片
 * 4. 评论时间
 * 5. 评论状态
 *
 * 注意：
 * 1. 评论创建需要验证订单状态
 * 2. 评论删除需要检查权限
 * 3. 评论更新需要记录修改历史
 * 4. 评论列表支持多种排序方式
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Comment
 */
public interface CommentService {
    
    /**
     * 获取所有评论列表
     *
     * 查询系统中的所有评论记录。
     * 返回结果按评论时间倒序排列。
     *
     * @return List<Comment> 评论列表，按时间倒序排列
     */
    List<Comment> getCommentList();
    
    /**
     * 创建评论
     *
     * 创建新的评论记录。
     * 创建过程包括：
     * 1. 验证订单状态
     * 2. 处理评论图片
     * 3. 保存评论信息
     * 4. 更新商品评分
     *
     * @param comment 评论对象，包含评论详细信息
     * @throws IllegalArgumentException 当评论信息不完整时抛出
     */
    void createComment(Comment comment);
    
    /**
     * 删除评论
     *
     * 删除指定的评论记录。
     * 删除前需要检查：
     * 1. 评论是否存在
     * 2. 用户是否有删除权限
     * 3. 评论是否被回复
     *
     * @param id 评论ID
     * @throws IllegalStateException 当评论不允许删除时抛出
     */
    void deleteComment(String id);

    /**
     * 更新评论
     *
     * 更新评论信息。
     * 更新内容包括：
     * 1. 评论内容
     * 2. 评论图片
     * 3. 评论状态
     *
     * @param comment 评论对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateComment(Comment comment);

    /**
     * 获取评论详情
     *
     * 查询指定评论的详细信息。
     * 包括评论内容、图片、回复等信息。
     *
     * @param id 评论ID
     * @return Comment 评论详情对象
     * @throws IllegalArgumentException 当评论ID为空时抛出
     */
    Comment getCommentDetail(String id);

    /**
     * 获取商品评论列表
     *
     * 查询指定商品的所有评论记录。
     * 支持按时间、评分等字段排序。
     *
     * @param thingId 商品ID
     * @param order 排序方式，可选值：time（时间）、score（评分）
     * @return List<Comment> 商品评论列表，按指定方式排序
     * @throws IllegalArgumentException 当商品ID为空时抛出
     */
    List<Comment> getThingCommentList(String thingId, String order);

    /**
     * 获取用户评论列表
     *
     * 查询指定用户的所有评论记录。
     * 返回结果按评论时间倒序排列。
     *
     * @param userId 用户ID
     * @return List<Comment> 用户评论列表，按时间倒序排列
     * @throws IllegalArgumentException 当用户ID为空时抛出
     */
    List<Comment> getUserCommentList(String userId);
}
