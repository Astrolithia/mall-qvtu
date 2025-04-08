package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    
    @Select("SELECT t.* FROM b_tag t JOIN b_thing_tag tt ON t.id = tt.tag_id WHERE tt.thing_id = #{thingId}")
    List<Tag> getTagsByThingId(@Param("thingId") String thingId);
}
