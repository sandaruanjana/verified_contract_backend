package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.ProjectImageDto;

import java.util.List;

public interface ProjectImageService {
    public List<ProjectImageDto> findAllByProjectId(String projectId);
}
