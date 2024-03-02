package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProjectDao {
    int save(Project project);
    Page<Project> findAll(Pageable page, String userId, int isPublic, int radius, String search, String category, String categoryOneId,
                          String categoryTwoId, String categoryThreeId);
    Page<Project> findAllByAssignUserId(Pageable page, String assignUserId, String status, String search);
    Page<Project> findAllPrivateByAssignUserId(Pageable page, String assignUserId, String status, String search);
    Page<Project> findAllByUserId(Pageable page, String userId, String status, String search);
    Optional<Project> findById(String id);
    int updateStatus(String id, String status);
    int updateStatusAndAssignUser(String id, String status, String assignUserId);
    int updateStatusAndRejectReason(String id, String status, String rejectReason);

    int delete(String id);
}
