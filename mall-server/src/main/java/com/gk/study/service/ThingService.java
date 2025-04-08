package com.gk.study.service;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Thing;

import java.math.BigDecimal;
import java.util.List;

public interface ThingService {
    List<Thing> getThingList(String keyword, String sort, String c, String tag);
    void createThing(Thing thing);
    void deleteThing(String id);

    void updateThing(Thing thing);

    Thing getThingById(String id);

    void addWishCount(String thingId);

    void addCollectCount(String thingId);
    
    void addSalesCount(String thingId, Integer count);
    
    void addScore(String thingId, BigDecimal score);
    
    void updateViewCount(String thingId);
    
    /**
     * 减少商品库存
     * @param thingId 商品ID
     * @param count 减少的数量
     * @return 是否成功减少库存
     */
    boolean reduceRepertory(String thingId, Integer count);
}
