package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ThingCollectMapper;
import com.gk.study.service.ThingCollectService;
import com.gk.study.entity.ThingCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品收藏服务实现类
 * 
 * 该类实现了商品收藏相关的业务逻辑，包括：
 * 1. 用户收藏商品列表的获取
 * 2. 商品收藏的创建和删除
 * 3. 判断用户是否已收藏某商品
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
class ThingCollectServiceImpl extends ServiceImpl<ThingCollectMapper, ThingCollect> implements ThingCollectService {
    
    /**
     * 商品收藏数据访问对象
     * 用于执行商品收藏相关的数据库操作
     */
    @Autowired
    ThingCollectMapper mapper;

    /**
     * 获取用户收藏的商品列表
     * 
     * 查询指定用户收藏的所有商品记录。
     * 返回的是Map列表，包含商品详细信息和收藏信息。
     * 
     * @param userId 用户ID
     * @return List<Map> 收藏商品列表，包含商品详情
     */
    @Override
    public List<Map> getThingCollectList(String userId) {
        return mapper.getThingCollectList(userId);
    }

    /**
     * 创建商品收藏
     * 
     * 创建新的商品收藏记录，记录用户对商品的收藏关系。
     * 
     * @param thingCollect 要创建的商品收藏对象，包含用户ID和商品ID
     */
    @Override
    public void createThingCollect(ThingCollect thingCollect) {
        mapper.insert(thingCollect);
    }

    /**
     * 删除商品收藏
     * 
     * 根据收藏记录ID删除指定的收藏关系。
     * 
     * @param id 要删除的收藏记录ID
     */
    @Override
    public void deleteThingCollect(String id) {
        mapper.deleteById(id);
    }

    /**
     * 获取用户对特定商品的收藏记录
     * 
     * 查询指定用户是否已收藏指定商品。
     * 通过用户ID和商品ID共同确定唯一的收藏记录。
     * 
     * @param userId 用户ID
     * @param thingId 商品ID
     * @return ThingCollect 收藏记录，如果不存在则返回null
     */
    @Override
    public ThingCollect getThingCollect(String userId, String thingId) {
        // 创建查询条件
        QueryWrapper<ThingCollect> queryWrapper = new QueryWrapper<>();
        // 设置商品ID和用户ID条件
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        // 执行查询
        return mapper.selectOne(queryWrapper);
    }
}
