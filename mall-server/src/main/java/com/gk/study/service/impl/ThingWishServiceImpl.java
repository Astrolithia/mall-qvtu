package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ThingWishMapper;
import com.gk.study.service.ThingWishService;
import com.gk.study.entity.ThingWish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品心愿服务实现类
 * 
 * 该类实现了商品心愿（收藏或加入愿望清单）相关的业务逻辑，包括：
 * 1. 用户心愿商品列表的获取
 * 2. 商品心愿的创建和删除
 * 3. 判断用户是否已将某商品加入心愿清单
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
class ThingWishServiceImpl extends ServiceImpl<ThingWishMapper, ThingWish> implements ThingWishService {
    
    /**
     * 商品心愿数据访问对象
     * 用于执行商品心愿相关的数据库操作
     */
    @Autowired
    ThingWishMapper mapper;

    /**
     * 获取用户心愿的商品列表
     * 
     * 查询指定用户添加到心愿清单的所有商品记录。
     * 返回的是Map列表，包含商品详细信息和心愿关系信息。
     * 
     * @param userId 用户ID
     * @return List<Map> 心愿商品列表，包含商品详情
     */
    @Override
    public List<Map> getThingWishList(String userId) {
        return mapper.getThingWishList(userId);
    }

    /**
     * 创建商品心愿
     * 
     * 创建新的商品心愿记录，记录用户对商品的心愿关系。
     * 添加到心愿清单后，商品的心愿数会增加。
     * 
     * @param thingWish 要创建的商品心愿对象，包含用户ID和商品ID
     */
    @Override
    public void createThingWish(ThingWish thingWish) {
        mapper.insert(thingWish);
    }

    /**
     * 删除商品心愿
     * 
     * 根据心愿记录ID删除指定的心愿关系。
     * 删除后，商品的心愿数应相应减少。
     * 
     * @param id 要删除的心愿记录ID
     */
    @Override
    public void deleteThingWish(String id) {
        mapper.deleteById(id);
    }

    /**
     * 获取用户对特定商品的心愿记录
     * 
     * 查询指定用户是否已将指定商品加入心愿清单。
     * 通过用户ID和商品ID共同确定唯一的心愿记录。
     * 
     * @param userId 用户ID
     * @param thingId 商品ID
     * @return ThingWish 心愿记录，如果不存在则返回null
     */
    @Override
    public ThingWish getThingWish(String userId, String thingId) {
        // 创建查询条件
        QueryWrapper<ThingWish> queryWrapper = new QueryWrapper<>();
        // 设置商品ID和用户ID条件
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        // 执行查询
        return mapper.selectOne(queryWrapper);
    }
}
