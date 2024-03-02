package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.ProjectProgressDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectProgressService {
    ProjectProgressDto save(ProjectProgressDto projectProgressDto);
    Page<ProjectProgressDto> findAll(Pageable page, String projectId, String search);
    public int findLastWeekNumberByProjectId(String projectId);
}
