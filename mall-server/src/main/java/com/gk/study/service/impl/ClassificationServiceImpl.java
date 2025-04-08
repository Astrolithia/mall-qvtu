package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Classification;
import com.gk.study.mapper.ClassificationMapper;
import com.gk.study.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 
 * 该类实现了分类相关的业务逻辑，包括：
 * 1. 分类列表的获取（平铺和树形结构）
 * 2. 分类的创建、删除和更新
 * 3. 分类的层级关系管理
 * 4. 分类路径的获取
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {
    
    /**
     * 分类数据访问对象
     * 用于执行分类相关的数据库操作
     */
    @Autowired
    ClassificationMapper mapper;

    /**
     * 获取分类列表（平铺结构）
     * 
     * 查询所有分类记录，按创建时间倒序排列。
     * 返回的列表包含所有分类，不区分层级关系。
     * 
     * @return List<Classification> 分类列表
     */
    @Override
    public List<Classification> getClassificationList() {
        // 创建查询条件
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 获取分类树形结构
     * 
     * 查询所有分类记录，并按层级关系组织成树形结构。
     * 返回的列表包含顶级分类及其子分类。
     * 
     * @return List<Classification> 分类树形结构
     */
    @Override
    public List<Classification> getClassificationTree() {
        // 获取所有分类
        List<Classification> allClassifications = getClassificationList();
        
        // 获取所有一级分类（parentId = 0）
        List<Classification> rootClassifications = allClassifications.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());
        
        // 按parentId分组所有非一级分类
        Map<Long, List<Classification>> childrenMap = allClassifications.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() != 0)
                .collect(Collectors.groupingBy(Classification::getParentId));
        
        // 递归设置子分类
        rootClassifications.forEach(root -> {
            setChildren(root, childrenMap);
        });
        
        return rootClassifications;
    }
    
    /**
     * 递归设置子分类
     */
    private void setChildren(Classification parent, Map<Long, List<Classification>> childrenMap) {
        List<Classification> children = childrenMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            children.forEach(child -> setChildren(child, childrenMap));
        } else {
            parent.setChildren(new ArrayList<>());
        }
    }

    /**
     * 获取子分类列表
     * 
     * 根据父分类ID查询其所有子分类。
     * 返回的列表按创建时间倒序排列。
     * 
     * @param parentId 父分类ID
     * @return List<Classification> 子分类列表
     */
    @Override
    public List<Classification> getChildClassifications(Long parentId) {
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        return mapper.selectList(queryWrapper);
    }

    /**
     * 创建分类
     * 
     * 创建新的分类记录，自动设置创建时间。
     * 如果指定了父分类ID，则建立父子关系。
     * 
     * @param classification 要创建的分类对象
     */
    @Override
    public void createClassification(Classification classification) {
        System.out.println(classification);
        
        // 设置分类层级
        if (classification.getParentId() == null || classification.getParentId() == 0) {
            // 一级分类
            classification.setParentId(0L);
            classification.setLevel(1);
        } else {
            // 获取父分类信息
            Classification parent = mapper.selectById(classification.getParentId());
            if (parent != null) {
                // 子分类层级 = 父分类层级 + 1
                classification.setLevel(parent.getLevel() + 1);
            } else {
                // 父分类不存在，默认设为一级分类
                classification.setParentId(0L);
                classification.setLevel(1);
            }
        }
        
        classification.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(classification);
    }

    /**
     * 删除分类
     * 
     * 根据分类ID删除指定的分类记录。
     * 注意：如果分类下有子分类，需要先删除子分类。
     * 
     * @param id 要删除的分类ID
     */
    @Override
    public void deleteClassification(String id) {
        // 先获取所有子分类
        List<Classification> children = getChildClassifications(Long.parseLong(id));
        
        // 递归删除所有子分类
        for (Classification child : children) {
            deleteClassification(String.valueOf(child.getId()));
        }
        
        // 删除当前分类
        mapper.deleteById(id);
    }

    /**
     * 更新分类
     * 
     * 更新指定分类的信息。
     * 使用分类ID作为更新条件。
     * 
     * @param classification 包含更新信息的分类对象
     */
    @Override
    public void updateClassification(Classification classification) {
        mapper.updateById(classification);
    }
    
    /**
     * 获取分类路径
     * 
     * 根据分类ID获取从根分类到当前分类的完整路径。
     * 返回的列表按层级顺序排列，从根分类到当前分类。
     * 
     * @param categoryId 分类ID
     * @return List<Classification> 分类路径列表
     */
    @Override
    public List<Classification> getClassificationPath(Long categoryId) {
        List<Classification> path = new ArrayList<>();
        
        // 查找当前分类
        Classification category = mapper.selectById(categoryId);
        if (category == null) {
            return path; // 分类不存在，返回空列表
        }
        
        // 添加当前分类到路径中
        path.add(category);
        
        // 递归查找所有父级分类
        findParentCategories(category.getParentId(), path);
        
        // 反转列表，使其从根分类到当前分类排列
        Collections.reverse(path);
        
        return path;
    }
    
    /**
     * 递归查找父级分类
     * @param parentId 父级分类ID
     * @param path 路径列表
     */
    private void findParentCategories(Long parentId, List<Classification> path) {
        if (parentId == null || parentId == 0) {
            return; // 已经到达一级分类，不再向上查找
        }
        
        Classification parent = mapper.selectById(parentId);
        if (parent != null) {
            // 添加父级分类到路径中
            path.add(parent);
            
            // 继续向上查找
            findParentCategories(parent.getParentId(), path);
        }
    }
}
