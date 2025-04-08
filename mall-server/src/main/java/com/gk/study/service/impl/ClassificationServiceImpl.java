package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ClassificationMapper;
import com.gk.study.entity.Classification;
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
 * @author Administrator
 * @date 2024-03-26
 */
@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {

    @Autowired
    ClassificationMapper mapper;

    @Override
    public List<Classification> getClassificationList() {
        return mapper.selectList(new QueryWrapper<>());
    }
    
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
    
    @Override
    public List<Classification> getChildClassifications(Long parentId) {
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        return mapper.selectList(queryWrapper);
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

    @Override
    public void updateClassification(Classification classification) {
        mapper.updateById(classification);
    }
    
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
