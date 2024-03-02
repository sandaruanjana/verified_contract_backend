package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDto save(ProjectDto projectDto);
    Page<ProjectDto> findAll(Pageable page, String userId, int isPublic, int radius, String search, String category, String categoryOneId,
                          String categoryTwoId, String categoryThreeId);
    Page<ProjectDto> findAllByAssignUserId(Pageable page, String assignUserId, String status, String search);
    Page<ProjectDto> findAllPrivateByAssignUserId(Pageable page, String assignUserId, String status, String search);
    Page<ProjectDto> findAllByUserId(Pageable page, String userId, String status, String search);
    ProjectDto updateStatus(String id, String status, String rejectReason);
    ProjectDto updateStatusAndAssignUser(String id, String status, String assignUserId);

    boolean delete(String id);
}
