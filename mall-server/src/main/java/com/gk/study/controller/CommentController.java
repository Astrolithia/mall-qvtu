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
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService service;

    @Autowired
    private UserScoreService userScoreService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Comment> list =  service.getCommentList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }


    // 商品的所有评论
    @RequestMapping(value = "/listThingComments", method = RequestMethod.GET)
    public APIResponse listThingComments(String thingId, String order){
        List<Comment> list =  service.getThingCommentList(thingId, order);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 用户的所有评论
    @RequestMapping(value = "/listUserComments", method = RequestMethod.GET)
    public APIResponse listUserComments(String userId){
        List<Comment> list =  service.getUserCommentList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Comment comment) throws IOException {
        // 创建评论
        service.createComment(comment);
        
        // 增加积分
        userScoreService.addCommentScore(comment.getUserId(), comment.getThingId(), comment.getOrderId());
        
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteComment(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Comment comment) throws IOException {
        service.updateComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @Transactional
    public APIResponse like(String id) throws IOException {
        Comment commentBean = service.getCommentDetail(id);
        int likeCount = Integer.parseInt(commentBean.getLikeCount()) + 1;
        commentBean.setLikeCount(String.valueOf(likeCount));
        service.updateComment(commentBean);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}
