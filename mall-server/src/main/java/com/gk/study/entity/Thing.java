package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Data
@TableName("b_thing")
public class Thing implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    @TableField
    public String title;
    @TableField
    public String cover;
    @TableField
    public String description;
    @TableField
    public BigDecimal price;
    @TableField
    public BigDecimal originalPrice;
    @TableField
    public String status;
    @TableField
    public Long createTime;
    @TableField
    public Long updateTime;
    @TableField
    public Long onShelfTime;
    @TableField
    public Long offShelfTime;
    @TableField
    public Integer repertory;
    @TableField
    public BigDecimal score;
    @TableField
    public Integer scoreCount;
    @TableField
    public BigDecimal totalScore;
    @TableField
    public Integer pv;
    @TableField
    public Integer viewCount;
    @TableField
    public Integer recommendCount;
    @TableField
    public Integer wishCount;
    @TableField
    public Integer collectCount;
    @TableField
    public Integer salesCount;
    @TableField
    public Long classificationId;

    @TableField(exist = false)
    public List<Long> tags; // 标签

    @TableField(exist = false)
    public String classification_title; // 分类名称

    @TableField(exist = false)
    public String classification_path;

    @TableField(exist = false)
    public List<Tag> tagList; // 完整标签对象列表

    @TableField(exist = false)
    public MultipartFile imageFile;

    public BigDecimal getAverageScore() {
        if (scoreCount == null || scoreCount == 0 || totalScore == null) {
            return BigDecimal.ZERO;
        }
        return totalScore.divide(new BigDecimal(scoreCount), 1, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDiscountRate() {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        return price.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getSavedAmount() {
        if (originalPrice == null) {
            return BigDecimal.ZERO;
        }
        return originalPrice.subtract(price);
    }

    public String getClassification_title() {
        return classification_title;
    }

    public void setClassification_title(String classification_title) {
        this.classification_title = classification_title;
    }

    public String getClassification_path() {
        return classification_path;
    }

    public void setClassification_path(String classification_path) {
        this.classification_path = classification_path;
    }

    public List<Tag> getTagList() {
        return tagList;
    }
}
