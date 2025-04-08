package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.AdMapper;
import com.gk.study.service.AdService;
import com.gk.study.entity.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 广告服务实现类
 * 
 * 该类实现了广告相关的业务逻辑，包括：
 * 1. 广告列表的获取
 * 2. 广告的创建、删除和更新
 * 3. 广告状态的管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {
    
    /**
     * 广告数据访问对象
     * 用于执行广告相关的数据库操作
     */
    @Autowired
    AdMapper mapper;

    /**
     * 获取广告列表
     * 
     * 查询所有广告记录，不添加任何筛选条件。
     * 返回的列表按创建时间倒序排列。
     * 
     * @return List<Ad> 广告列表
     */
    @Override
    public List<Ad> getAdList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 创建广告
     * 
     * 创建新的广告记录，自动设置创建时间。
     * 打印广告信息到控制台用于调试。
     * 
     * @param ad 要创建的广告对象
     */
    @Override
    public void createAd(Ad ad) {
        // 打印广告信息，用于调试
        System.out.println(ad);
        // 设置创建时间
        ad.setCreateTime(String.valueOf(System.currentTimeMillis()));
        // 插入广告记录
        mapper.insert(ad);
    }

    /**
     * 删除广告
     * 
     * 根据广告ID删除指定的广告记录。
     * 
     * @param id 要删除的广告ID
     */
    @Override
    public void deleteAd(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新广告
     * 
     * 更新指定广告的信息。
     * 使用广告ID作为更新条件。
     * 
     * @param ad 包含更新信息的广告对象
     */
    @Override
    public void updateAd(Ad ad) {
        mapper.updateById(ad);
    }
}
