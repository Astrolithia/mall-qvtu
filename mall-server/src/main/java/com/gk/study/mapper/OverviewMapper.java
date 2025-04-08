package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.VisitData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Mapper
public interface OverviewMapper extends BaseMapper<VisitData> {

    List<Object> getPopularThing();
    List<Object> getPopularClassification();
    List<VisitData> getWebVisitData(@Param("day") String day);

}
