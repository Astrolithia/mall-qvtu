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
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    TagMapper mapper;

    @Autowired
    ThingTagMapper thingTagMapper;

    @Override
    public List<Tag> getTagList() {
        return mapper.selectList(null);
    }

    @Override
    public void createTag(Tag tag) {
        mapper.insert(tag);
    }

    @Override
    public void deleteTag(String id) {
        // 删除标签前，先删除商品与标签的关联
        QueryWrapper<ThingTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_id", id);
        thingTagMapper.delete(queryWrapper);
        
        // 删除标签
        mapper.deleteById(id);
    }

    @Override
    public void updateTag(Tag tag) {
        mapper.updateById(tag);
    }

    @Override
    public List<Tag> getTagsByThingId(String thingId) {
        return mapper.getTagsByThingId(thingId);
    }
}
