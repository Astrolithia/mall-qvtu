package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;

/**
 * 广告实体类
 *
 * 该类用于表示系统中的广告信息，包括广告图片、链接等基本信息。
 * 使用MyBatis-Plus注解进行数据库映射，支持图片文件上传功能。
 * 
 * 主要属性包括：
 * 1. 广告ID
 * 2. 广告图片
 * 3. 广告链接
 * 4. 创建时间
 * 
 * 注意：
 * 1. 使用@TableName注解指定数据库表名
 * 2. 使用@TableId注解指定主键生成策略
 * 3. 使用@TableField注解进行字段映射
 * 4. 使用@Data注解自动生成getter/setter等方法
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Data
@TableName("b_ad")
public class Ad implements Serializable {

    /**
     * 广告ID
     * 主键，使用自增策略生成
     */
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;

    /**
     * 广告图片路径
     * 存储上传后的图片文件路径
     */
    @TableField
    public String image;

    /**
     * 广告图片文件
     * 用于接收上传的图片文件
     * 使用@TableField(exist = false)注解标记为非数据库字段
     */
    @TableField(exist = false)
    public MultipartFile imageFile;

    /**
     * 广告链接
     * 点击广告后跳转的URL地址
     */
    @TableField
    public String link;

    /**
     * 创建时间
     * 记录广告的创建时间
     */
    @TableField
    public String createTime;
}
