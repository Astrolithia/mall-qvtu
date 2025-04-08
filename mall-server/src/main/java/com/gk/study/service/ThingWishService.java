package com.gk.study.service;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.ThingWish;

import java.util.List;
import java.util.Map;

public interface ThingWishService {
    List<Map> getThingWishList(String userId);
    void createThingWish(ThingWish thingWish);
    void deleteThingWish(String id);

    ThingWish getThingWish(String userId, String thingId);
}
