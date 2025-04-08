package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Tag;
import com.gk.study.entity.ThingTag;
import com.gk.study.mapper.TagMapper;
import com.gk.study.mapper.ThingTagMapper;
import com.gk.study.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 标签服务实现类
 * 
 * 该类实现了标签相关的业务逻辑，包括：
 * 1. 标签列表的获取
 * 2. 标签的创建、删除和更新
 * 3. 根据商品ID获取标签
 * 4. 商品标签关联管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    
    /**
     * 标签数据访问对象
     * 用于执行标签相关的数据库操作
     */
    @Autowired
    TagMapper mapper;

    /**
     * 商品标签关联数据访问对象
     * 用于执行商品与标签关联的数据库操作
     */
    @Autowired
    ThingTagMapper thingTagMapper;

    /**
     * 获取标签列表
     * 
     * 查询所有标签记录，不添加任何筛选条件。
     * 返回的列表包含所有标签信息。
     * 
     * @return List<Tag> 标签列表
     */
    @Override
    public List<Tag> getTagList() {
        return mapper.selectList(null);
    }

    /**
     * 创建标签
     * 
     * 创建新的标签记录。
     * 
     * @param tag 要创建的标签对象
     */
    @Override
    public void createTag(Tag tag) {
        mapper.insert(tag);
    }

    /**
     * 删除标签
     * 
     * 根据标签ID删除指定的标签记录。
     * 删除标签前，先删除该标签与商品的所有关联关系，防止出现孤立数据。
     * 
     * @param id 要删除的标签ID
     */
    @Override
    public void deleteTag(String id) {
        // 删除标签前，先删除商品与标签的关联
        QueryWrapper<ThingTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_id", id);
        thingTagMapper.delete(queryWrapper);
        
        // 删除标签
        mapper.deleteById(id);
    }

    /**
     * 更新标签
     * 
     * 更新指定标签的信息。
     * 使用标签ID作为更新条件。
     * 
     * @param tag 包含更新信息的标签对象
     */
    @Override
    public void updateTag(Tag tag) {
        mapper.updateById(tag);
    }

    /**
     * 根据商品ID获取标签列表
     * 
     * 查询指定商品关联的所有标签。
     * 通过商品-标签关联表进行多表查询。
     * 
     * @param thingId 商品ID
     * @return List<Tag> 商品关联的标签列表
     */
    @Override
    public List<Tag> getTagsByThingId(String thingId) {
        return mapper.getTagsByThingId(thingId);
    }
}
