package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.ProjectProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectProgressDao {
    int save(ProjectProgress projectProgress);
    Page<ProjectProgress> findAll(Pageable page, String projectId, String search);
    int findLastWeekNumberByProjectId(String projectId);

}
