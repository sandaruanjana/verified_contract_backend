package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.SkillDto;

import java.util.List;

public interface SkillService {
    List<SkillDto> findAllByType(String type);
}
