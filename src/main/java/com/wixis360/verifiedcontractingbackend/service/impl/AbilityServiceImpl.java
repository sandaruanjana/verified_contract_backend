package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.AbilityDao;
import com.wixis360.verifiedcontractingbackend.dto.AbilityDto;
import com.wixis360.verifiedcontractingbackend.model.Ability;
import com.wixis360.verifiedcontractingbackend.service.AbilityService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class AbilityServiceImpl implements AbilityService {
    private AbilityDao abilityDao;
    private ModelMapper mapper;

    @Override
    public List<AbilityDto> findAll() {
        return abilityDao.findAll().stream().map(this::getAbilityDto).toList();
    }

    private AbilityDto getAbilityDto(Ability ability) {
        return mapper.map(ability, AbilityDto.class);
    }
}
