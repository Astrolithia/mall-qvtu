package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Comment;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.CommentService;
import com.gk.study.service.UserScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 评论管理控制器
 *
 * 该控制器负责处理与商品评论相关的所有HTTP请求，包括评论的创建、查询、更新和删除等操作。
 * 支持评论的点赞功能，并实现了用户积分奖励机制。
 * 
 * 主要功能包括：
 * 1. 查询评论列表
 * 2. 查询商品评论
 * 3. 查询用户评论
 * 4. 创建评论（包含积分奖励）
 * 5. 删除评论
 * 6. 更新评论
 * 7. 评论点赞
 * 
 * 注意：
 * 1. 删除和更新操作需要管理员权限
 * 2. 创建评论会自动增加用户积分
 * 3. 评论支持按商品和用户分类查询
 * 4. 使用事务管理确保数据一致性
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    /**
     * 日志记录器
     * 用于记录控制器操作和异常信息，便于系统监控和问题排查
     */
    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    /**
     * 评论服务接口
     * 处理与评论相关的业务逻辑，包括评论的CRUD操作
     */
    @Autowired
    CommentService service;

    /**
     * 用户积分服务接口
     * 处理用户积分相关的业务逻辑，包括评论积分的奖励
     */
    @Autowired
    private UserScoreService userScoreService;

    /**
     * 获取所有评论列表
     * 
     * 该方法返回系统中所有的评论记录，用于前端展示。
     * 评论列表按创建时间倒序排列，确保最新的评论优先显示。
     *
     * @return APIResponse 包含状态码、提示信息和评论列表数据的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        // 记录开始查询日志
        logger.info("开始查询评论列表");
        
        // 调用服务层获取评论列表
        List<Comment> list = service.getCommentList();
        
        // 记录查询结果日志
        logger.info("查询评论列表完成, 记录数量={}", list.size());
        
        // 返回成功响应，包含评论列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 获取指定商品的所有评论
     * 
     * 该方法返回指定商品的所有评论记录。
     * 支持按不同方式排序，如按时间、点赞数等。
     *
     * @param thingId 要查询的商品ID
     * @param order 排序方式，如"time"表示按时间排序，"like"表示按点赞数排序
     * @return APIResponse 包含状态码、提示信息和商品评论列表的响应对象
     */
    @RequestMapping(value = "/listThingComments", method = RequestMethod.GET)
    public APIResponse listThingComments(String thingId, String order){
        // 记录开始查询日志
        logger.info("开始查询商品评论, thingId={}, order={}", thingId, order);
        
        // 调用服务层获取商品评论列表
        List<Comment> list = service.getThingCommentList(thingId, order);
        
        // 记录查询结果日志
        logger.info("查询商品评论完成, 记录数量={}", list.size());
        
        // 返回成功响应，包含商品评论列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 获取指定用户的所有评论
     * 
     * 该方法返回指定用户发表的所有评论记录。
     * 用于展示用户的评论历史。
     *
     * @param userId 要查询的用户ID
     * @return APIResponse 包含状态码、提示信息和用户评论列表的响应对象
     */
    @RequestMapping(value = "/listUserComments", method = RequestMethod.GET)
    public APIResponse listUserComments(String userId){
        // 记录开始查询日志
        logger.info("开始查询用户评论, userId={}", userId);
        
        // 调用服务层获取用户评论列表
        List<Comment> list = service.getUserCommentList(userId);
        
        // 记录查询结果日志
        logger.info("查询用户评论完成, 记录数量={}", list.size());
        
        // 返回成功响应，包含用户评论列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新评论
     * 
     * 该方法处理创建新评论的请求。
     * 创建评论后会自动增加用户积分，作为用户参与互动的奖励。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param comment 包含评论信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Comment comment) throws IOException {
        // 记录开始创建评论的日志
        logger.info("开始创建新评论: {}", comment);
        
        // 创建评论记录
        service.createComment(comment);
        
        // 增加用户积分
        userScoreService.addCommentScore(comment.getUserId(), comment.getThingId(), comment.getOrderId());
        
        // 记录创建成功的日志
        logger.info("评论创建成功: {}", comment);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除评论
     * 
     * 该方法处理删除评论的请求，支持批量删除多个评论。
     * 需要管理员权限才能执行此操作。
     * 删除操作会同时删除评论记录和相关的积分记录。
     *
     * @param ids 要删除的评论ID列表，多个ID用逗号分隔
     * @return APIResponse 包含操作结果的响应对象
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的评论ID
        logger.info("开始删除评论, ids={}", ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除评论
        for (String id : arr) {
            service.deleteComment(id);
        }
        
        // 记录删除成功的日志
        logger.info("评论删除成功, ids={}", ids);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新评论信息
     * 
     * 该方法处理更新现有评论的请求。
     * 需要管理员权限才能执行此操作。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param comment 包含更新后评论信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Comment comment) throws IOException {
        // 记录开始更新评论的日志
        logger.info("开始更新评论: {}", comment);
        
        // 更新评论记录
        service.updateComment(comment);
        
        // 记录更新成功的日志
        logger.info("评论更新成功: {}", comment);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 评论点赞
     * 
     * 该方法处理评论点赞的请求。
     * 点赞操作会增加评论的点赞数，用于衡量评论的热度。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param id 要点赞的评论ID
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @Transactional
    public APIResponse like(String id) throws IOException {
        // 记录开始点赞的日志
        logger.info("开始点赞评论, id={}", id);
        
        // 获取评论详情
        Comment commentBean = service.getCommentDetail(id);
        
        // 增加点赞数
        int likeCount = Integer.parseInt(commentBean.getLikeCount()) + 1;
        commentBean.setLikeCount(String.valueOf(likeCount));
        
        // 更新评论
        service.updateComment(commentBean);
        
        // 记录点赞成功的日志
        logger.info("评论点赞成功, id={}, 当前点赞数={}", id, likeCount);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}
