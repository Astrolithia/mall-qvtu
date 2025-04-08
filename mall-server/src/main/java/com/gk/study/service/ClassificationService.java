package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Classification;

import java.util.List;

/**
 * 分类服务接口
 * @author Administrator
 * @date 2024-03-26
 */
public interface ClassificationService {
    /**
     * 获取所有分类列表（扁平结构）
     */
    List<Classification> getClassificationList();
    
    /**
     * 获取树形分类列表，按层级组织
     */
    List<Classification> getClassificationTree();
    
    /**
     * 获取指定父分类下的所有子分类
     * @param parentId 父分类ID
     */
    List<Classification> getChildClassifications(Long parentId);
    
    void createClassification(Classification Classification);
    void deleteClassification(String id);

    void updateClassification(Classification classification);

    /**
     * 获取指定分类ID的完整分类路径（包含所有父级分类）
     * @param categoryId 分类ID
     * @return 从根分类到当前分类的完整路径列表
     */
    List<Classification> getClassificationPath(Long categoryId);
}
