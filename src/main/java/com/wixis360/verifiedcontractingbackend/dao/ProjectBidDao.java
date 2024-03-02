package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.ProjectBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProjectBidDao {
    int save(ProjectBid projectBid);
    Page<ProjectBid> findAllByProjectId(Pageable page, String projectId, String search);
    Optional<ProjectBid> findByProjectIdAndUserId(String projectId, String userId);
    int deleteByProjectIdAndUserId(String projectId, String userId);
}
