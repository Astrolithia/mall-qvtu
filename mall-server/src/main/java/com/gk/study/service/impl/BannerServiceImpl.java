package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Banner;
import com.gk.study.mapper.BannerMapper;
import com.gk.study.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务实现类
 * 
 * 该类实现了轮播图相关的业务逻辑，包括：
 * 1. 轮播图列表的获取
 * 2. 轮播图的创建、删除和更新
 * 3. 轮播图的排序和状态管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
    
    /**
     * 轮播图数据访问对象
     * 用于执行轮播图相关的数据库操作
     */
    @Autowired
    BannerMapper mapper;

    /**
     * 获取轮播图列表
     * 
     * 查询所有轮播图记录。
     * 返回的列表按创建时间倒序排列。
     * 
     * @return List<Banner> 轮播图列表
     */
    @Override
    public List<Banner> getBannerList() {
        // 创建查询条件
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 创建轮播图
     * 
     * 创建新的轮播图记录，自动设置创建时间。
     * 打印轮播图信息到控制台用于调试。
     * 
     * @param banner 要创建的轮播图对象
     */
    @Override
    public void createBanner(Banner banner) {
        // 打印轮播图信息，用于调试
        System.out.println(banner);
        // 设置创建时间
        banner.setCreateTime(String.valueOf(System.currentTimeMillis()));
        // 插入轮播图记录
        mapper.insert(banner);
    }

    /**
     * 删除轮播图
     * 
     * 根据轮播图ID删除指定的轮播图记录。
     * 
     * @param id 要删除的轮播图ID
     */
    @Override
    public void deleteBanner(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新轮播图
     * 
     * 更新指定轮播图的信息。
     * 使用轮播图ID作为更新条件。
     * 
     * @param banner 包含更新信息的轮播图对象
     */
    @Override
    public void updateBanner(Banner banner) {
        mapper.updateById(banner);
    }
}
