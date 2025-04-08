package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户积分记录实体类
 * @author Administrator
 * @date 2024-03-26
 */
@Data
@TableName("b_user_score")
public class UserScore implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    
    @TableField
    public String userId;
    
    @TableField
    public BigDecimal score;
    
    @TableField
    public String type; // 积分类型：ORDER_CONFIRM(确认收货), COMMENT(评价), SIGN_IN(每日签到)
    
    @TableField
    public String description; // 积分描述
    
    @TableField
    public Long createTime;
    
    @TableField
    public String orderId; // 关联订单ID（如果是订单相关积分）
    
    @TableField
    public String thingId; // 关联商品ID（如果是评价相关积分）
} 