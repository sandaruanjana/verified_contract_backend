package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.SkillDao;
import com.wixis360.verifiedcontractingbackend.dto.SkillDto;
import com.wixis360.verifiedcontractingbackend.model.Skill;
import com.wixis360.verifiedcontractingbackend.service.SkillService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {
    private SkillDao skillDao;
    private ModelMapper mapper;

    @Override
    public List<SkillDto> findAllByType(String type) {
        return skillDao.findAllByType(type).stream().map(this::getSkillDto).toList();
    }

    private SkillDto getSkillDto(Skill skill) {
        return mapper.map(skill, SkillDto.class);
    }
}
