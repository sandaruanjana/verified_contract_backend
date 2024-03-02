package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectProgressImage {
    private String id;
    private String projectProgressId;
    private String projectId;
    private String name;
    private String description;
    private Date uploadTime;
}
