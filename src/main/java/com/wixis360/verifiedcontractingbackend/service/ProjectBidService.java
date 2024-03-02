package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.ProjectBidDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectBidService {
    ProjectBidDto save(ProjectBidDto projectBidDto);
    Page<ProjectBidDto> findAllByProjectId(Pageable page, String projectId, String search);
}
