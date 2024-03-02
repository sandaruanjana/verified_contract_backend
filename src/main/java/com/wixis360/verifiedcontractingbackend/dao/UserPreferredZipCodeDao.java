package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.UserPreferredZipCode;

import java.util.List;

public interface UserPreferredZipCodeDao {
    int save(UserPreferredZipCode userPreferredZipCode);
    List<UserPreferredZipCode> findAllByUserId(String userId);
    int deleteByUserId(String userId);
}
