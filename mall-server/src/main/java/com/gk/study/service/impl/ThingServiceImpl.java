package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Classification;
import com.gk.study.entity.Comment;
import com.gk.study.entity.Thing;
import com.gk.study.entity.ThingTag;
import com.gk.study.entity.Tag;
import com.gk.study.mapper.ClassificationMapper;
import com.gk.study.mapper.CommentMapper;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.mapper.ThingTagMapper;
import com.gk.study.service.TagService;
import com.gk.study.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    private static final Logger log = LoggerFactory.getLogger(ThingServiceImpl.class);
    
    @Autowired
    ThingMapper mapper;

    @Autowired
    ThingTagMapper thingTagMapper;

    @Autowired
    ClassificationMapper classificationMapper;
    
    @Autowired
    CommentMapper commentMapper;

    @Autowired
    TagService tagService;

    @Override
    public List<Thing> getThingList(String keyword, String sort, String c, String tag) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();

        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 排序
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equals("recent")) {
                queryWrapper.orderBy(true, false, "create_time");
            } else if (sort.equals("hot") || sort.equals("recommend")) {
                queryWrapper.orderBy(true, false, "pv");
            } else if (sort.equals("sales")) {
                queryWrapper.orderBy(true, false, "sales_count"); // 按销量排序
            } else if (sort.equals("price_asc")) {
                queryWrapper.orderBy(true, true, "price"); // 价格从低到高
            } else if (sort.equals("price_desc")) {
                queryWrapper.orderBy(true, false, "price"); // 价格从高到低
            } else if (sort.equals("score")) {
                queryWrapper.orderBy(true, false, "score"); // 按评分排序
            }
        } else {
            queryWrapper.orderBy(true, false, "create_time");
        }

        // 根据分类筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            // 获取选中分类及其所有子分类ID
            List<Long> categoryIds = new ArrayList<>();
            categoryIds.add(Long.parseLong(c)); // 添加当前选中的分类ID
            
            // 递归获取所有子分类ID
            List<Classification> allCategories = classificationMapper.selectList(null);
            addChildCategories(Long.parseLong(c), allCategories, categoryIds);
            
            // 使用IN条件查询指定分类及其子分类下的商品
            queryWrapper.in("classification_id", categoryIds);
            
            log.info("分类筛选 - 分类ID: {}，包含子分类ID: {}", c, categoryIds);
        }

        List<Thing> things = mapper.selectList(queryWrapper);

        // tag筛选
        if (StringUtils.isNotBlank(tag)) {
            List<Thing> tThings = new ArrayList<>();
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.eq("tag_id", tag);
            List<ThingTag> thingTagList = thingTagMapper.selectList(thingTagQueryWrapper);
            for (Thing thing : things) {
                for (ThingTag thingTag : thingTagList) {
                    if (thing.getId().equals(thingTag.getThingId())) {
                        tThings.add(thing);
                    }
                }
            }
            things.clear();
            things.addAll(tThings);
        }

        // 为每个商品附加分类名称和标签信息
        for (Thing thing : things) {
            // 获取分类信息
            if (thing.getClassificationId() != null) {
                // 查询分类名称
                QueryWrapper<Classification> classQueryWrapper = new QueryWrapper<>();
                classQueryWrapper.eq("id", thing.getClassificationId());
                Classification classification = classificationMapper.selectOne(classQueryWrapper);
                if (classification != null) {
                    thing.setClassification_title(classification.getTitle());
                    
                    // 构建分类路径信息，用于前端显示完整分类层级
                    StringBuilder pathBuilder = new StringBuilder();
                    buildClassificationPath(pathBuilder, classification);
                    if (pathBuilder.length() > 0) {
                        thing.setClassification_path(pathBuilder.toString());
                    }
                }
            }
            
            // 获取标签信息
            List<Tag> tags = tagService.getTagsByThingId(String.valueOf(thing.getId()));
            if (tags != null && !tags.isEmpty()) {
                thing.setTagList(tags);
                List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
                thing.setTags(tagIds);
            }
            
            // 确保评分正确计算
            if (thing.getScoreCount() != null && thing.getScoreCount() > 0 && thing.getTotalScore() != null) {
                // 手动计算平均评分，确保前端显示正确
                thing.setScore(thing.getAverageScore());
            } else {
                // 从评论表中获取评分数据
                try {
                    List<Comment> comments = commentMapper.getCommentsByThingId(String.valueOf(thing.getId()));
                    if (comments != null && !comments.isEmpty()) {
                        // 统计评论中的评分
                        BigDecimal totalScore = BigDecimal.ZERO;
                        int validCommentCount = 0;
                        
                        for (Comment comment : comments) {
                            if (comment.getScore() != null) {
                                // 确保类型转换正确
                                try {
                                    BigDecimal commentScore = new BigDecimal(comment.getScore().toString());
                                    if (commentScore.compareTo(BigDecimal.ZERO) > 0) {
                                        totalScore = totalScore.add(commentScore);
                                        validCommentCount++;
                                    }
                                } catch (NumberFormatException e) {
                                    log.warn("评论分数格式错误: {}", comment.getScore());
                                }
                            }
                        }
                        
                        if (validCommentCount > 0) {
                            // 计算平均分
                            BigDecimal avgScore = totalScore.divide(new BigDecimal(validCommentCount), 1, BigDecimal.ROUND_HALF_UP);
                            thing.setScore(avgScore);
                            thing.setScoreCount(validCommentCount);
                            thing.setTotalScore(totalScore);
                        } else {
                            // 没有有效评分
                            thing.setScore(BigDecimal.ZERO);
                            thing.setScoreCount(0);
                        }
                    } else {
                        // 没有评论
                        thing.setScore(BigDecimal.ZERO);
                        thing.setScoreCount(0);
                    }
                } catch (Exception e) {
                    log.error("计算商品评分出错: " + e.getMessage(), e);
                    // 异常时设为0分
                    thing.setScore(BigDecimal.ZERO);
                    thing.setScoreCount(0);
                }
            }
        }
        
        return things;
    }

    @Override
    public void createThing(Thing thing) {
        System.out.println(thing);
        long currentTime = System.currentTimeMillis();
        thing.setCreateTime(currentTime);
        thing.setUpdateTime(currentTime);

        // 设置默认值
        if (thing.getPv() == null) {
            thing.setPv(0);
        }
        if (thing.getScore() == null) {
            thing.setScore(new BigDecimal("0"));
        }
        if (thing.getScoreCount() == null) {
            thing.setScoreCount(0);
        }
        if (thing.getTotalScore() == null) {
            thing.setTotalScore(new BigDecimal("0"));
        }
        if (thing.getWishCount() == null) {
            thing.setWishCount(0);
        }
        if (thing.getCollectCount() == null) {
            thing.setCollectCount(0);
        }
        if (thing.getRecommendCount() == null) {
            thing.setRecommendCount(0);
        }
        if (thing.getSalesCount() == null) {
            thing.setSalesCount(0);
        }
        
        // 处理原价
        if (thing.getOriginalPrice() == null || thing.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            // 如果未设置原价或原价<=0，则将原价设为当前价格
            thing.setOriginalPrice(thing.getPrice());
        }
        
        mapper.insert(thing);
        // 更新tag
        setThingTags(thing);
    }

    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateThing(Thing thing) {
        // 更新修改时间
        thing.setUpdateTime(System.currentTimeMillis());
        
        // 更新tag
        setThingTags(thing);

        mapper.updateById(thing);
    }

    @Override
    public Thing getThingById(String id) {
        // 增加PV
        Thing thing = mapper.selectById(id);
        if (thing != null) {
            thing.setPv(thing.getPv() + 1);
            
            // 获取分类信息
            if (thing.getClassificationId() != null) {
                // 查询分类名称
                QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", thing.getClassificationId());
                Classification classification = classificationMapper.selectOne(queryWrapper);
                if (classification != null) {
                    thing.setClassification_title(classification.getTitle());
                    
                    // 构建分类路径信息，用于前端显示完整分类层级
                    StringBuilder pathBuilder = new StringBuilder();
                    buildClassificationPath(pathBuilder, classification);
                    if (pathBuilder.length() > 0) {
                        thing.setClassification_path(pathBuilder.toString());
                    }
                }
            }
            
            // 获取标签信息
            List<Tag> tags = tagService.getTagsByThingId(String.valueOf(thing.getId()));
            if (tags != null && !tags.isEmpty()) {
                // 设置完整的标签对象列表
                thing.setTagList(tags);
                List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
                thing.setTags(tagIds);
            }
            
            // 确保评分正确计算
            if (thing.getScoreCount() != null && thing.getScoreCount() > 0 && thing.getTotalScore() != null) {
                // 手动计算平均评分，确保前端显示正确
                thing.setScore(thing.getAverageScore());
            } else {
                // 从评论表中获取评分数据
                try {
                    List<Comment> comments = commentMapper.getCommentsByThingId(String.valueOf(thing.getId()));
                    if (comments != null && !comments.isEmpty()) {
                        // 统计评论中的评分
                        BigDecimal totalScore = BigDecimal.ZERO;
                        int validCommentCount = 0;
                        
                        for (Comment comment : comments) {
                            if (comment.getScore() != null) {
                                // 确保类型转换正确
                                try {
                                    BigDecimal commentScore = new BigDecimal(comment.getScore().toString());
                                    if (commentScore.compareTo(BigDecimal.ZERO) > 0) {
                                        totalScore = totalScore.add(commentScore);
                                        validCommentCount++;
                                    }
                                } catch (NumberFormatException e) {
                                    log.warn("评论分数格式错误: {}", comment.getScore());
                                }
                            }
                        }
                        
                        if (validCommentCount > 0) {
                            // 计算平均分
                            BigDecimal avgScore = totalScore.divide(new BigDecimal(validCommentCount), 1, BigDecimal.ROUND_HALF_UP);
                            thing.setScore(avgScore);
                            thing.setScoreCount(validCommentCount);
                            thing.setTotalScore(totalScore);
                        } else {
                            // 没有有效评分
                            thing.setScore(BigDecimal.ZERO);
                            thing.setScoreCount(0);
                        }
                    } else {
                        // 没有评论
                        thing.setScore(BigDecimal.ZERO);
                        thing.setScoreCount(0);
                    }
                } catch (Exception e) {
                    log.error("计算商品评分出错: " + e.getMessage(), e);
                    // 异常时设为0分
                    thing.setScore(BigDecimal.ZERO);
                    thing.setScoreCount(0);
                }
            }
            
            mapper.updateById(thing);
        }
        return thing;
    }

    // 心愿数加1
    @Override
    public void addWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            thing.setWishCount(thing.getWishCount() + 1);
            mapper.updateById(thing);
        }
    }

    // 收藏数加1
    @Override
    public void addCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            thing.setCollectCount(thing.getCollectCount() + 1);
            mapper.updateById(thing);
        }
    }
    
    // 增加销量
    @Override
    public void addSalesCount(String thingId, Integer count) {
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            thing.setSalesCount(thing.getSalesCount() + count);
            mapper.updateById(thing);
        }
    }
    
    // 添加评分
    @Override
    public void addScore(String thingId, BigDecimal score) {
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            thing.setScoreCount(thing.getScoreCount() + 1);
            thing.setTotalScore(thing.getTotalScore().add(score));
            // 重新计算平均分
            thing.setScore(thing.getAverageScore());
            mapper.updateById(thing);
        }
    }

    // 更新浏览量
    @Override
    public void updateViewCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 如果viewCount字段为null，初始化为1，否则加1
            Integer viewCount = thing.getViewCount();
            if (viewCount == null) {
                viewCount = 1;
            } else {
                viewCount += 1;
            }
            thing.setViewCount(viewCount);
            mapper.updateById(thing);
        }
    }

    /**
     * 减少商品库存
     * @param thingId 商品ID
     * @param count 减少的数量
     * @return 是否成功减少库存
     */
    @Override
    public boolean reduceRepertory(String thingId, Integer count) {
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 检查库存是否足够
            if (thing.getRepertory() < count) {
                return false; // 库存不足
            }
            
            // 减少库存
            thing.setRepertory(thing.getRepertory() - count);
            mapper.updateById(thing);
            return true;
        }
        return false; // 商品不存在
    }

    public void setThingTags(Thing thing) {
        // 删除tag
        Map<String, Object> map = new HashMap<>();
        map.put("thing_id", thing.getId());
        thingTagMapper.deleteByMap(map);
        // 新增tag
        if (thing.getTags() != null) {
            for (Long tag : thing.getTags()) {
                ThingTag thingTag = new ThingTag();
                thingTag.setThingId(thing.getId());
                thingTag.setTagId(tag);
                thingTagMapper.insert(thingTag);
            }
        }
    }

    /**
     * 递归构建分类路径
     * @param pathBuilder 路径字符串构建器
     * @param classification 当前分类
     */
    private void buildClassificationPath(StringBuilder pathBuilder, Classification classification) {
        if (classification == null) {
            return;
        }
        
        // 如果有父级分类，先处理父级
        if (classification.getParentId() != null && classification.getParentId() > 0) {
            Classification parentClassification = classificationMapper.selectById(classification.getParentId());
            if (parentClassification != null) {
                buildClassificationPath(pathBuilder, parentClassification);
                pathBuilder.append(">");
            }
        }
        
        // 添加当前分类名称
        pathBuilder.append(classification.getTitle());
    }

    private void addChildCategories(long categoryId, List<Classification> allCategories, List<Long> categoryIds) {
        for (Classification classification : allCategories) {
            if (classification.getParentId() == categoryId) {
                categoryIds.add(classification.getId());
                addChildCategories(classification.getId(), allCategories, categoryIds);
            }
        }
    }
}
