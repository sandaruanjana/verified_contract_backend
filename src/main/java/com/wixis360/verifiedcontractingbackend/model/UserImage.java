package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserImage {
    private String id;
    private String userId;
    private String projectImageId;
    private String name;
    private String description;
    private int isPublic;
    private Date uploadTime;
}
