package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Notice;

import java.util.List;

/**
 * 通知服务接口
 * @author Administrator
 * @date 2024-03-26
 */
public interface NoticeService {
    List<Notice> getNoticeList();
    void createNotice(Notice notice);
    void deleteNotice(String id);

    void updateNotice(Notice notice);
}
