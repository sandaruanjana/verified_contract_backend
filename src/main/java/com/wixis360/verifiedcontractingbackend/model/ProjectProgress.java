package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectProgress {
    private String id;
    private String projectId;
    private int week;
    private String title;
    private String description;
    private Date createdTime;
}
