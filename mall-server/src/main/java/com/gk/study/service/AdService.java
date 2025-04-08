package com.gk.study.service;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Ad;

import java.util.List;

public interface AdService {
    List<Ad> getAdList();
    void createAd(Ad ad);
    void deleteAd(String id);

    void updateAd(Ad ad);
}
