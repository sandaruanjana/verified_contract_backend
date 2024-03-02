package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.ProjectImage;

import java.util.List;

public interface ProjectImageDao {
    int save(ProjectImage projectImage);
    List<ProjectImage> findAllByProjectId(String projectId);
}
