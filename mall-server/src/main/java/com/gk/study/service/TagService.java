package com.gk.study.service;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getTagList();
    void createTag(Tag tag);
    void deleteTag(String id);

    void updateTag(Tag tag);
    
    List<Tag> getTagsByThingId(String thingId);
}
