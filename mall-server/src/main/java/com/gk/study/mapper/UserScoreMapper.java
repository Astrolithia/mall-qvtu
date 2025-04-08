package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.UserScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户积分记录Mapper接口
 * @author Administrator
 * @date 2024-03-26
 */
@Mapper
public interface UserScoreMapper extends BaseMapper<UserScore> {
    
    /**
     * 获取用户积分记录列表
     * @param userId 用户ID
     * @return 积分记录列表
     */
    @Select("SELECT * FROM b_user_score WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserScore> getUserScoreList(@Param("userId") String userId);
    
    /**
     * 获取用户总积分
     * @param userId 用户ID
     * @return 总积分
     */
    @Select("SELECT COALESCE(SUM(score), 0) FROM b_user_score WHERE user_id = #{userId}")
    BigDecimal getUserTotalScore(@Param("userId") String userId);
} 