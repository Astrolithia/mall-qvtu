package com.gk.study.service;
/**
 * 轮播图服务接口
 *
 * 该接口定义了轮播图相关的所有业务操作。
 * 主要功能包括：
 * 1. 轮播图的创建、删除和更新
 * 2. 轮播图列表的获取
 *
 * 轮播图属性说明：
 * 1. 轮播图图片：支持多种图片格式
 * 2. 轮播图链接：点击后的跳转地址
 * 3. 轮播图顺序：控制显示顺序
 * 4. 轮播图状态：控制显示和隐藏
 *
 * 注意：
 * 1. 轮播图创建需要上传图片文件
 * 2. 轮播图更新需要处理图片更新
 * 3. 轮播图删除需要同时删除图片文件
 * 4. 轮播图列表需要按顺序排序
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Banner
 */

import com.gk.study.entity.Banner;

import java.util.List;

public interface BannerService {
    /**
     * 获取轮播图列表
     *
     * 查询系统中的所有轮播图记录。
     * 返回结果按顺序和创建时间排序。
     *
     * @return List<Banner> 轮播图列表，按顺序和创建时间排序
     */
    List<Banner> getBannerList();
    
    /**
     * 创建轮播图
     *
     * 创建新的轮播图记录。
     * 创建过程包括：
     * 1. 上传轮播图图片
     * 2. 保存轮播图信息
     * 3. 设置轮播图顺序
     *
     * @param banner 轮播图对象，包含轮播图详细信息
     * @throws IllegalArgumentException 当轮播图信息不完整时抛出
     */
    void createBanner(Banner banner);
    
    /**
     * 删除轮播图
     *
     * 删除指定的轮播图记录。
     * 删除过程包括：
     * 1. 删除轮播图图片文件
     * 2. 删除轮播图记录
     *
     * @param id 轮播图ID
     * @throws IllegalArgumentException 当轮播图ID为空时抛出
     */
    void deleteBanner(String id);

    /**
     * 更新轮播图
     *
     * 更新轮播图信息。
     * 更新内容包括：
     * 1. 轮播图基本信息
     * 2. 轮播图图片（可选）
     * 3. 轮播图顺序
     * 4. 轮播图状态
     *
     * @param banner 轮播图对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateBanner(Banner banner);
}
