package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getList();

    List<Comment> selectThingCommentList(String thingId, String order);

    List<Comment> selectUserCommentList(String userId);
    
    /**
     * 根据商品ID获取所有评论，用于计算评分
     * @param thingId 商品ID
     * @return 评论列表
     */
    @Select("SELECT * FROM b_comment WHERE thing_id = #{thingId}")
    List<Comment> getCommentsByThingId(@Param("thingId") String thingId);
}
