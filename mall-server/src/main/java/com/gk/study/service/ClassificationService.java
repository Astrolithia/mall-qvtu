package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Classification;

import java.util.List;

/**
 * 分类服务接口
 *
 * 该接口定义了商品分类相关的所有业务操作。
 * 主要功能包括：
 * 1. 分类的创建、删除和更新
 * 2. 分类列表的获取（扁平结构和树形结构）
 * 3. 子分类的获取
 * 4. 分类路径的获取
 *
 * 分类属性说明：
 * 1. 分类名称
 * 2. 父分类ID
 * 3. 分类级别
 * 4. 分类排序
 * 5. 分类状态
 *
 * 注意：
 * 1. 分类支持多级结构
 * 2. 分类删除需要检查是否被商品使用
 * 3. 分类更新需要处理父子关系
 * 4. 分类排序影响显示顺序
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Classification
 */
public interface ClassificationService {
    /**
     * 获取所有分类列表（扁平结构）
     *
     * 查询系统中的所有分类记录。
     * 返回结果按分类级别和排序字段排序。
     *
     * @return List<Classification> 分类列表，按级别和排序字段排序
     */
    List<Classification> getClassificationList();
    
    /**
     * 获取树形分类列表，按层级组织
     *
     * 查询系统中的所有分类记录，并按层级组织成树形结构。
     * 返回结果是一个树形结构的分类列表。
     *
     * @return List<Classification> 树形结构的分类列表
     */
    List<Classification> getClassificationTree();
    
    /**
     * 获取指定父分类下的所有子分类
     *
     * 查询指定父分类下的所有直接子分类。
     * 返回结果按排序字段排序。
     *
     * @param parentId 父分类ID
     * @return List<Classification> 子分类列表，按排序字段排序
     * @throws IllegalArgumentException 当父分类ID为空时抛出
     */
    List<Classification> getChildClassifications(Long parentId);
    
    /**
     * 创建分类
     *
     * 创建新的分类记录。
     * 创建过程包括：
     * 1. 验证分类信息完整性
     * 2. 处理父子关系
     * 3. 设置分类级别
     * 4. 保存分类信息
     *
     * @param classification 分类对象，包含分类详细信息
     * @throws IllegalArgumentException 当分类信息不完整时抛出
     */
    void createClassification(Classification classification);
    
    /**
     * 删除分类
     *
     * 删除指定的分类记录。
     * 删除前需要检查：
     * 1. 分类是否存在
     * 2. 分类是否被商品使用
     * 3. 分类是否有子分类
     *
     * @param id 分类ID
     * @throws IllegalStateException 当分类被使用时抛出
     */
    void deleteClassification(String id);

    /**
     * 更新分类
     *
     * 更新分类信息。
     * 更新内容包括：
     * 1. 分类基本信息
     * 2. 分类排序
     * 3. 分类状态
     *
     * @param classification 分类对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateClassification(Classification classification);

    /**
     * 获取指定分类ID的完整分类路径
     *
     * 查询从根分类到指定分类的完整路径。
     * 返回结果按层级顺序排列，从根分类到当前分类。
     *
     * @param categoryId 分类ID
     * @return List<Classification> 从根分类到当前分类的完整路径列表
     * @throws IllegalArgumentException 当分类ID为空时抛出
     */
    List<Classification> getClassificationPath(Long categoryId);
}
