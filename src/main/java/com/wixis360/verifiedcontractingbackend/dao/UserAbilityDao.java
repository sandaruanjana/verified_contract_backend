package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.UserAbility;

import java.util.List;

public interface UserAbilityDao {
    int save(UserAbility userAbility);
    List<UserAbility> findAllByUserId(String userId);
    int deleteByUserId(String userId);
}
