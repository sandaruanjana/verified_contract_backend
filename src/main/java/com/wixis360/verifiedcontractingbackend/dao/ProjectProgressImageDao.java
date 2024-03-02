package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.ProjectProgressImage;

import java.util.List;

public interface ProjectProgressImageDao {
    int save(ProjectProgressImage projectProgressImage);
    List<ProjectProgressImage> findAllByProjectProgressId(String projectProgressId);
}
