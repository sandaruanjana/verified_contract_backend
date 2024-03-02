package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.Skill;

import java.util.List;

public interface SkillDao {
    List<Skill> findAllByType(String type);
}
