package com.gk.study.service;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddressList(String userId);
    void createAddress(Address address);
    void deleteAddress(String id);

    void updateAddress(Address address);
}
