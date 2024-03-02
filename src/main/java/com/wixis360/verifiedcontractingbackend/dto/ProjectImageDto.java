package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectImageDto {
    private String id;
    private String projectId;
    private String name;
    private String description;
    private Date uploadTime;
}
