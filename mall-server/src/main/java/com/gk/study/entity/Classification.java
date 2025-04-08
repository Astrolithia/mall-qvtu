package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * 分类实体类
 * @author Administrator
 * @date 2024-03-26
 */
@Data
@TableName("b_classification")
public class Classification implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    @TableField
    public String title;
    @TableField
    public String createTime;
    
    @TableField
    public Long parentId;
    
    @TableField
    public Integer level;
    
    @TableField(exist = false)
    public List<Classification> children;
}
