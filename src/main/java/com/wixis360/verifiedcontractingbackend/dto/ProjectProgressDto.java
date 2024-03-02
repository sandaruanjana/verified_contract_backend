package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ProjectProgressDto {
    private String id;
    private String projectId;
    private int week;
    private String title;
    private String description;
    private Date createdTime;
    private List<ProjectProgressImageDto> images = new ArrayList<>();
}
