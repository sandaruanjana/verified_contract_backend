package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.UserSkill;

import java.util.List;

public interface UserSkillDao {
    int save(UserSkill userSkill);
    List<UserSkill> findAllByUserId(String userId);
    int deleteByUserId(String userId);
}
