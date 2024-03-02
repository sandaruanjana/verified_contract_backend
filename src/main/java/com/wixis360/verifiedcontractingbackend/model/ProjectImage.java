package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectImage {
    private String id;
    private String projectId;
    private String name;
    private String description;
    private Date uploadTime;
}
