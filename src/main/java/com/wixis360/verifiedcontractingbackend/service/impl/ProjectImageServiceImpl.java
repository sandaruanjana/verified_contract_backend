package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectImageDao;
import com.wixis360.verifiedcontractingbackend.dto.ProjectImageDto;
import com.wixis360.verifiedcontractingbackend.model.ProjectImage;
import com.wixis360.verifiedcontractingbackend.service.ProjectImageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ProjectImageServiceImpl implements ProjectImageService {
    private final ProjectImageDao projectImageDao;
    private final ModelMapper mapper;

    @Override
    public List<ProjectImageDto> findAllByProjectId(String projectId) {
        return projectImageDao.findAllByProjectId(projectId).stream().map(this::getProjectImageDto).toList();
    }

    private ProjectImageDto getProjectImageDto(ProjectImage projectImage) {
        return mapper.map(projectImage, ProjectImageDto.class);
    }
}
