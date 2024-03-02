package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectProgressImageDto {
    private String id;
    private String projectProgressId;
    private String projectId;
    private String name;
    private String description;
    private Date uploadTime;
}
