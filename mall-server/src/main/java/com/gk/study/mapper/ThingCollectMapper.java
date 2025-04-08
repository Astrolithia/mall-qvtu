package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.ThingCollect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Mapper
public interface ThingCollectMapper extends BaseMapper<ThingCollect> {


    List<Map> getThingCollectList(String userId);
}
