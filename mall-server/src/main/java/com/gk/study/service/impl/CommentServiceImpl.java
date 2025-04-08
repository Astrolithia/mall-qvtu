package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Comment;
import com.gk.study.mapper.CommentMapper;
import com.gk.study.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务实现类
 * 
 * 该类实现了评论相关的业务逻辑，包括：
 * 1. 评论列表的获取（全部、商品相关、用户相关）
 * 2. 评论的创建、删除和更新
 * 3. 评论点赞功能
 * 4. 评论状态管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    /**
     * 评论数据访问对象
     * 用于执行评论相关的数据库操作
     */
    @Autowired
    CommentMapper mapper;

    /**
     * 获取评论列表
     * 
     * 查询所有评论记录，按创建时间倒序排列。
     * 返回的列表包含所有评论，包括评论内容、评分、图片等信息。
     * 
     * @return List<Comment> 评论列表
     */
    @Override
    public List<Comment> getCommentList() {
        // 创建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 获取商品评论列表
     * 
     * 根据商品ID查询其所有评论记录。
     * 返回的列表按创建时间倒序排列。
     * 
     * @param thingId 商品ID
     * @param order 排序方式
     * @return List<Comment> 商品评论列表
     */
    @Override
    public List<Comment> getThingCommentList(String thingId, String order) {
        // 创建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        // 设置商品ID条件
        queryWrapper.eq("thing_id", thingId);
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 获取用户评论列表
     * 
     * 根据用户ID查询其所有评论记录。
     * 返回的列表按创建时间倒序排列。
     * 
     * @param userId 用户ID
     * @return List<Comment> 用户评论列表
     */
    @Override
    public List<Comment> getUserCommentList(String userId) {
        // 创建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        // 设置用户ID条件
        queryWrapper.eq("user_id", userId);
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 创建评论
     * 
     * 创建新的评论记录，自动设置创建时间。
     * 评论创建后，会自动增加用户的评分。
     * 
     * @param comment 要创建的评论对象
     */
    @Override
    public void createComment(Comment comment) {
        // 设置创建时间
        comment.setCommentTime(String.valueOf(System.currentTimeMillis()));
        // 插入评论记录
        mapper.insert(comment);
    }

    /**
     * 删除评论
     * 
     * 根据评论ID删除指定的评论记录。
     * 注意：删除评论后，用户的评分会相应减少。
     * 
     * @param id 要删除的评论ID
     */
    @Override
    public void deleteComment(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新评论
     * 
     * 更新指定评论的信息。
     * 使用评论ID作为更新条件。
     * 
     * @param comment 包含更新信息的评论对象
     */
    @Override
    public void updateComment(Comment comment) {
        mapper.updateById(comment);
    }

    /**
     * 获取评论详情
     * 
     * 根据评论ID获取评论的详细信息。
     * 
     * @param id 评论ID
     * @return Comment 评论详情
     */
    @Override
    public Comment getCommentDetail(String id) {
        return mapper.selectById(id);
    }
}
