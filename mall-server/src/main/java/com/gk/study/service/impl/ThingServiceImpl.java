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
 * 商品服务实现类
 * 
 * 该类实现了商品相关的业务逻辑，包括：
 * 1. 商品列表的获取和筛选（搜索、排序、分类、标签）
 * 2. 商品的创建、删除、更新和查询
 * 3. 商品统计数据的管理（心愿数、收藏数、销量、评分、浏览量）
 * 4. 商品分类和标签的关联管理
 * 5. 商品库存的管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 商品的评分基于用户评论，通过计算评论的平均分自动更新。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    
    /**
     * 日志记录器
     * 用于记录系统日志信息
     */
    private static final Logger log = LoggerFactory.getLogger(ThingServiceImpl.class);
    
    /**
     * 商品数据访问对象
     * 用于执行商品相关的数据库操作
     */
    @Autowired
    ThingMapper mapper;

    /**
     * 商品标签关联数据访问对象
     * 用于执行商品与标签关联的数据库操作
     */
    @Autowired
    ThingTagMapper thingTagMapper;

    /**
     * 分类数据访问对象
     * 用于执行分类相关的数据库操作
     */
    @Autowired
    ClassificationMapper classificationMapper;
    
    /**
     * 评论数据访问对象
     * 用于执行评论相关的数据库操作
     */
    @Autowired
    CommentMapper commentMapper;

    /**
     * 标签服务
     * 用于获取标签相关的业务逻辑
     */
    @Autowired
    TagService tagService;

    /**
     * 获取商品列表
     * 
     * 查询商品记录，支持多种筛选条件：
     * 1. 关键字搜索（标题模糊匹配）
     * 2. 排序方式（最新、最热、销量、价格、评分）
     * 3. 分类筛选（包含子分类）
     * 4. 标签筛选
     * 
     * 为每个商品附加额外信息：
     * 1. 分类名称和分类路径
     * 2. 标签列表
     * 3. 评分计算
     * 
     * @param keyword 搜索关键字
     * @param sort 排序方式（recent:最新, hot/recommend:最热, sales:销量, price_asc:价格从低到高, price_desc:价格从高到低, score:评分）
     * @param c 分类ID
     * @param tag 标签ID
     * @return List<Thing> 商品列表
     */
    @Override
    public List<Thing> getThingList(String keyword, String sort, String c, String tag) {
        // 创建查询条件
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();

        // 搜索处理：根据关键字对标题进行模糊查询
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 排序处理：根据不同的排序参数设置不同的排序规则
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equals("recent")) {
                // 最新：按创建时间倒序
                queryWrapper.orderBy(true, false, "create_time");
            } else if (sort.equals("hot") || sort.equals("recommend")) {
                // 最热/推荐：按浏览量倒序
                queryWrapper.orderBy(true, false, "pv");
            } else if (sort.equals("sales")) {
                // 销量：按销量倒序
                queryWrapper.orderBy(true, false, "sales_count");
            } else if (sort.equals("price_asc")) {
                // 价格从低到高：按价格升序
                queryWrapper.orderBy(true, true, "price");
            } else if (sort.equals("price_desc")) {
                // 价格从高到低：按价格降序
                queryWrapper.orderBy(true, false, "price");
            } else if (sort.equals("score")) {
                // 评分：按评分倒序
                queryWrapper.orderBy(true, false, "score");
            }
        } else {
            // 默认排序：按创建时间倒序
            queryWrapper.orderBy(true, false, "create_time");
        }

        // 分类筛选：包含指定分类及其所有子分类
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            // 获取选中分类及其所有子分类ID
            List<Long> categoryIds = new ArrayList<>();
            categoryIds.add(Long.parseLong(c)); // 添加当前选中的分类ID
            
            // 递归获取所有子分类ID
            List<Classification> allCategories = classificationMapper.selectList(null);
            addChildCategories(Long.parseLong(c), allCategories, categoryIds);
            
            // 使用IN条件查询指定分类及其子分类下的商品
            queryWrapper.in("classification_id", categoryIds);
            
            // 记录分类筛选信息
            log.info("分类筛选 - 分类ID: {}，包含子分类ID: {}", c, categoryIds);
        }

        // 执行商品查询
        List<Thing> things = mapper.selectList(queryWrapper);

        // 标签筛选：筛选包含指定标签的商品
        if (StringUtils.isNotBlank(tag)) {
            // 创建临时列表存储筛选结果
            List<Thing> tThings = new ArrayList<>();
            // 查询包含指定标签的商品关联关系
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.eq("tag_id", tag);
            List<ThingTag> thingTagList = thingTagMapper.selectList(thingTagQueryWrapper);
            // 筛选匹配的商品
            for (Thing thing : things) {
                for (ThingTag thingTag : thingTagList) {
                    if (thing.getId().equals(thingTag.getThingId())) {
                        tThings.add(thing);
                    }
                }
            }
            // 更新结果列表
            things.clear();
            things.addAll(tThings);
        }

        // 为每个商品附加额外信息
        for (Thing thing : things) {
            // 附加分类信息
            if (thing.getClassificationId() != null) {
                // 查询分类名称
                QueryWrapper<Classification> classQueryWrapper = new QueryWrapper<>();
                classQueryWrapper.eq("id", thing.getClassificationId());
                Classification classification = classificationMapper.selectOne(classQueryWrapper);
                if (classification != null) {
                    // 设置分类名称
                    thing.setClassification_title(classification.getTitle());
                    
                    // 构建分类路径信息，用于前端显示完整分类层级
                    StringBuilder pathBuilder = new StringBuilder();
                    buildClassificationPath(pathBuilder, classification);
                    if (pathBuilder.length() > 0) {
                        thing.setClassification_path(pathBuilder.toString());
                    }
                }
            }
            
            // 附加标签信息
            List<Tag> tags = tagService.getTagsByThingId(String.valueOf(thing.getId()));
            if (tags != null && !tags.isEmpty()) {
                // 设置标签对象列表
                thing.setTagList(tags);
                // 设置标签ID列表
                List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
                thing.setTags(tagIds);
            }
            
            // 计算评分
            if (thing.getScoreCount() != null && thing.getScoreCount() > 0 && thing.getTotalScore() != null) {
                // 如果已有评分记录，手动计算平均评分，确保前端显示正确
                thing.setScore(thing.getAverageScore());
            } else {
                // 如果没有评分记录，从评论表中获取评分数据
                try {
                    // 查询商品的所有评论
                    List<Comment> comments = commentMapper.getCommentsByThingId(String.valueOf(thing.getId()));
                    if (comments != null && !comments.isEmpty()) {
                        // 统计评论中的评分
                        BigDecimal totalScore = BigDecimal.ZERO;
                        int validCommentCount = 0;
                        
                        // 遍历评论，累加评分
                        for (Comment comment : comments) {
                            if (comment.getScore() != null) {
                                // 确保类型转换正确
                                try {
                                    BigDecimal commentScore = new BigDecimal(comment.getScore().toString());
                                    if (commentScore.compareTo(BigDecimal.ZERO) > 0) {
                                        // 累加有效评分
                                        totalScore = totalScore.add(commentScore);
                                        validCommentCount++;
                                    }
                                } catch (NumberFormatException e) {
                                    // 记录评分格式错误
                                    log.warn("评论分数格式错误: {}", comment.getScore());
                                }
                            }
                        }
                        
                        // 计算并设置最终评分
                        if (validCommentCount > 0) {
                            // 计算平均分，保留1位小数
                            BigDecimal avgScore = totalScore.divide(new BigDecimal(validCommentCount), 1, BigDecimal.ROUND_HALF_UP);
                            thing.setScore(avgScore);
                            thing.setScoreCount(validCommentCount);
                            thing.setTotalScore(totalScore);
                        } else {
                            // 没有有效评分，设为0分
                            thing.setScore(BigDecimal.ZERO);
                            thing.setScoreCount(0);
                        }
                    } else {
                        // 没有评论，设为0分
                        thing.setScore(BigDecimal.ZERO);
                        thing.setScoreCount(0);
                    }
                } catch (Exception e) {
                    // 记录评分计算错误
                    log.error("计算商品评分出错: " + e.getMessage(), e);
                    // 异常时设为0分
                    thing.setScore(BigDecimal.ZERO);
                    thing.setScoreCount(0);
                }
            }
        }
        
        return things;
    }

    /**
     * 创建商品
     * 
     * 创建新的商品记录，设置创建时间、更新时间和默认值。
     * 默认值包括：浏览量、评分、心愿数、收藏数、推荐数、销量等。
     * 同时处理商品的标签关联关系。
     * 
     * @param thing 要创建的商品对象
     */
    @Override
    public void createThing(Thing thing) {
        // 打印商品信息（调试用）
        System.out.println(thing);
        
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();
        // 设置创建时间和更新时间
        thing.setCreateTime(currentTime);
        thing.setUpdateTime(currentTime);

        // 设置各项默认值
        if (thing.getPv() == null) {
            // 默认浏览量为0
            thing.setPv(0);
        }
        if (thing.getScore() == null) {
            // 默认评分为0
            thing.setScore(new BigDecimal("0"));
        }
        if (thing.getScoreCount() == null) {
            // 默认评分数为0
            thing.setScoreCount(0);
        }
        if (thing.getTotalScore() == null) {
            // 默认总评分为0
            thing.setTotalScore(new BigDecimal("0"));
        }
        if (thing.getWishCount() == null) {
            // 默认心愿数为0
            thing.setWishCount(0);
        }
        if (thing.getCollectCount() == null) {
            // 默认收藏数为0
            thing.setCollectCount(0);
        }
        if (thing.getRecommendCount() == null) {
            // 默认推荐数为0
            thing.setRecommendCount(0);
        }
        if (thing.getSalesCount() == null) {
            // 默认销量为0
            thing.setSalesCount(0);
        }
        
        // 处理原价
        if (thing.getOriginalPrice() == null || thing.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            // 如果未设置原价或原价<=0，则将原价设为当前价格
            thing.setOriginalPrice(thing.getPrice());
        }
        
        // 插入商品记录
        mapper.insert(thing);
        // 更新商品标签关联
        setThingTags(thing);
    }

    /**
     * 删除商品
     * 
     * 根据商品ID删除指定的商品记录。
     * 
     * @param id 要删除的商品ID
     */
    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新商品
     * 
     * 更新指定商品的信息，自动更新修改时间。
     * 同时处理商品的标签关联关系。
     * 
     * @param thing 包含更新信息的商品对象
     */
    @Override
    public void updateThing(Thing thing) {
        // 更新修改时间
        thing.setUpdateTime(System.currentTimeMillis());
        
        // 更新商品标签关联
        setThingTags(thing);

        // 更新商品记录
        mapper.updateById(thing);
    }

    /**
     * 获取商品详情
     * 
     * 根据商品ID获取商品的详细信息，并自动增加浏览量。
     * 附加商品的分类信息、标签信息和评分计算。
     * 
     * @param id 商品ID
     * @return Thing 商品详情
     */
    @Override
    public Thing getThingById(String id) {
        // 查询商品
        Thing thing = mapper.selectById(id);
        if (thing != null) {
            // 增加浏览量
            thing.setPv(thing.getPv() + 1);
            
            // 附加分类信息
            if (thing.getClassificationId() != null) {
                // 查询分类
                QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", thing.getClassificationId());
                Classification classification = classificationMapper.selectOne(queryWrapper);
                if (classification != null) {
                    // 设置分类名称
                    thing.setClassification_title(classification.getTitle());
                    
                    // 构建分类路径
                    StringBuilder pathBuilder = new StringBuilder();
                    buildClassificationPath(pathBuilder, classification);
                    if (pathBuilder.length() > 0) {
                        thing.setClassification_path(pathBuilder.toString());
                    }
                }
            }
            
            // 附加标签信息
            List<Tag> tags = tagService.getTagsByThingId(String.valueOf(thing.getId()));
            if (tags != null && !tags.isEmpty()) {
                // 设置完整的标签对象列表
                thing.setTagList(tags);
                // 设置标签ID列表
                List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
                thing.setTags(tagIds);
            }
            
            // 计算评分
            if (thing.getScoreCount() != null && thing.getScoreCount() > 0 && thing.getTotalScore() != null) {
                // 如果已有评分记录，手动计算平均评分
                thing.setScore(thing.getAverageScore());
            } else {
                // 从评论表中获取评分数据
                try {
                    // 查询商品的所有评论
                    List<Comment> comments = commentMapper.getCommentsByThingId(String.valueOf(thing.getId()));
                    if (comments != null && !comments.isEmpty()) {
                        // 统计评论中的评分
                        BigDecimal totalScore = BigDecimal.ZERO;
                        int validCommentCount = 0;
                        
                        // 遍历评论，累加评分
                        for (Comment comment : comments) {
                            if (comment.getScore() != null) {
                                // 确保类型转换正确
                                try {
                                    BigDecimal commentScore = new BigDecimal(comment.getScore().toString());
                                    if (commentScore.compareTo(BigDecimal.ZERO) > 0) {
                                        // 累加有效评分
                                        totalScore = totalScore.add(commentScore);
                                        validCommentCount++;
                                    }
                                } catch (NumberFormatException e) {
                                    // 记录评分格式错误
                                    log.warn("评论分数格式错误: {}", comment.getScore());
                                }
                            }
                        }
                        
                        // 计算并设置最终评分
                        if (validCommentCount > 0) {
                            // 计算平均分，保留1位小数
                            BigDecimal avgScore = totalScore.divide(new BigDecimal(validCommentCount), 1, BigDecimal.ROUND_HALF_UP);
                            thing.setScore(avgScore);
                            thing.setScoreCount(validCommentCount);
                            thing.setTotalScore(totalScore);
                        } else {
                            // 没有有效评分，设为0分
                            thing.setScore(BigDecimal.ZERO);
                            thing.setScoreCount(0);
                        }
                    } else {
                        // 没有评论，设为0分
                        thing.setScore(BigDecimal.ZERO);
                        thing.setScoreCount(0);
                    }
                } catch (Exception e) {
                    // 记录评分计算错误
                    log.error("计算商品评分出错: " + e.getMessage(), e);
                    // 异常时设为0分
                    thing.setScore(BigDecimal.ZERO);
                    thing.setScoreCount(0);
                }
            }
            
            // 更新商品记录
            mapper.updateById(thing);
        }
        return thing;
    }

    /**
     * 增加心愿数
     * 
     * 将指定商品的心愿数加1。
     * 
     * @param thingId 商品ID
     */
    @Override
    public void addWishCount(String thingId) {
        // 查询商品
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 心愿数加1
            thing.setWishCount(thing.getWishCount() + 1);
            // 更新商品
            mapper.updateById(thing);
        }
    }

    /**
     * 增加收藏数
     * 
     * 将指定商品的收藏数加1。
     * 
     * @param thingId 商品ID
     */
    @Override
    public void addCollectCount(String thingId) {
        // 查询商品
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 收藏数加1
            thing.setCollectCount(thing.getCollectCount() + 1);
            // 更新商品
            mapper.updateById(thing);
        }
    }
    
    /**
     * 增加销量
     * 
     * 将指定商品的销量增加指定数量。
     * 
     * @param thingId 商品ID
     * @param count 增加的数量
     */
    @Override
    public void addSalesCount(String thingId, Integer count) {
        // 查询商品
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 销量增加
            thing.setSalesCount(thing.getSalesCount() + count);
            // 更新商品
            mapper.updateById(thing);
        }
    }
    
    /**
     * 添加评分
     * 
     * 为指定商品添加新的评分，并重新计算平均分。
     * 
     * @param thingId 商品ID
     * @param score 评分值
     */
    @Override
    public void addScore(String thingId, BigDecimal score) {
        // 查询商品
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 评分数加1
            thing.setScoreCount(thing.getScoreCount() + 1);
            // 总评分增加
            thing.setTotalScore(thing.getTotalScore().add(score));
            // 重新计算平均分
            thing.setScore(thing.getAverageScore());
            // 更新商品
            mapper.updateById(thing);
        }
    }

    /**
     * 更新浏览量
     * 
     * 将指定商品的浏览量加1。
     * 
     * @param thingId 商品ID
     */
    @Override
    public void updateViewCount(String thingId) {
        // 查询商品
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
            // 更新商品
            mapper.updateById(thing);
        }
    }

    /**
     * 减少商品库存
     * 
     * 将指定商品的库存减少指定数量。
     * 会检查库存是否足够，不足则返回失败。
     * 
     * @param thingId 商品ID
     * @param count 减少的数量
     * @return boolean 是否成功减少库存
     */
    @Override
    public boolean reduceRepertory(String thingId, Integer count) {
        // 查询商品
        Thing thing = mapper.selectById(thingId);
        if (thing != null) {
            // 检查库存是否足够
            if (thing.getRepertory() < count) {
                return false; // 库存不足
            }
            
            // 减少库存
            thing.setRepertory(thing.getRepertory() - count);
            // 更新商品
            mapper.updateById(thing);
            return true;
        }
        return false; // 商品不存在
    }

    /**
     * 设置商品标签
     * 
     * 更新商品的标签关联关系，先删除原有关联，再创建新关联。
     * 
     * @param thing 商品对象，包含ID和标签列表
     */
    public void setThingTags(Thing thing) {
        // 删除原有标签关联
        Map<String, Object> map = new HashMap<>();
        map.put("thing_id", thing.getId());
        thingTagMapper.deleteByMap(map);
        
        // 创建新的标签关联
        if (thing.getTags() != null) {
            for (Long tag : thing.getTags()) {
                // 创建关联对象
                ThingTag thingTag = new ThingTag();
                thingTag.setThingId(thing.getId());
                thingTag.setTagId(tag);
                // 插入关联记录
                thingTagMapper.insert(thingTag);
            }
        }
    }

    /**
     * 递归构建分类路径
     * 
     * 通过递归方式构建完整的分类路径，格式为"一级分类>二级分类>三级分类"。
     * 
     * @param pathBuilder 路径字符串构建器
     * @param classification 当前分类
     */
    private void buildClassificationPath(StringBuilder pathBuilder, Classification classification) {
        if (classification == null) {
            return;
        }
        
        // 如果有父级分类，先处理父级
        if (classification.getParentId() != null && classification.getParentId() > 0) {
            // 查询父级分类
            Classification parentClassification = classificationMapper.selectById(classification.getParentId());
            if (parentClassification != null) {
                // 递归处理父级分类
                buildClassificationPath(pathBuilder, parentClassification);
                // 添加分隔符
                pathBuilder.append(">");
            }
        }
        
        // 添加当前分类名称
        pathBuilder.append(classification.getTitle());
    }

    /**
     * 递归添加子分类
     * 
     * 递归查找指定分类的所有子分类，并添加到分类ID列表中。
     * 
     * @param categoryId 父分类ID
     * @param allCategories 所有分类列表
     * @param categoryIds 分类ID结果列表
     */
    private void addChildCategories(long categoryId, List<Classification> allCategories, List<Long> categoryIds) {
        for (Classification classification : allCategories) {
            if (classification.getParentId() == categoryId) {
                // 添加子分类ID
                categoryIds.add(classification.getId());
                // 递归处理子分类的子分类
                addChildCategories(classification.getId(), allCategories, categoryIds);
            }
        }
    }
}
