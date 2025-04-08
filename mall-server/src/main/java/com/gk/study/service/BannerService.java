package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */

import com.gk.study.entity.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> getBannerList();
    void createBanner(Banner banner);
    void deleteBanner(String id);

    void updateBanner(Banner banner);
}
