package com.gk.study.service;

import com.gk.study.entity.Ad;
import java.util.List;

/**
 * 广告服务接口
 *
 * 该接口定义了广告相关的所有业务操作。
 * 主要功能包括：
 * 1. 广告的创建、删除和更新
 * 2. 广告列表的获取
 *
 * 广告属性说明：
 * 1. 广告图片：支持多种图片格式
 * 2. 广告链接：点击广告后的跳转地址
 * 3. 广告状态：控制广告的显示和隐藏
 *
 * 注意：
 * 1. 广告创建需要上传图片文件
 * 2. 广告更新需要处理图片更新
 * 3. 广告删除需要同时删除图片文件
 * 4. 广告列表需要按优先级排序
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Ad
 */
public interface AdService {
    
    /**
     * 获取广告列表
     *
     * 查询系统中的所有广告记录。
     * 返回结果按优先级和创建时间排序。
     *
     * @return List<Ad> 广告列表，按优先级和创建时间排序
     */
    List<Ad> getAdList();
    
    /**
     * 创建广告
     *
     * 创建新的广告记录。
     * 创建过程包括：
     * 1. 上传广告图片
     * 2. 保存广告信息
     * 3. 设置广告状态
     *
     * @param ad 广告对象，包含广告详细信息
     * @throws IllegalArgumentException 当广告信息不完整时抛出
     */
    void createAd(Ad ad);
    
    /**
     * 删除广告
     *
     * 删除指定的广告记录。
     * 删除过程包括：
     * 1. 删除广告图片文件
     * 2. 删除广告记录
     *
     * @param id 广告ID
     * @throws IllegalArgumentException 当广告ID为空时抛出
     */
    void deleteAd(String id);

    /**
     * 更新广告
     *
     * 更新广告信息。
     * 更新内容包括：
     * 1. 广告基本信息
     * 2. 广告图片（可选）
     * 3. 广告状态
     *
     * @param ad 广告对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateAd(Ad ad);
}
