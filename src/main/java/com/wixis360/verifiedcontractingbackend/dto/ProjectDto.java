package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectDto {
    private String id;
    private String userId;
    private String assignUserId;
    private String name;
    private Date preferredDate;
    private String addressLine1;
    private String addressLine2;
    private String nature;
    private String zipCode;
    private String longitude;
    private String latitude;
    private String category;
    private String categoryOneId;
    private String categoryTwoId;
    private String categoryThreeId;
    private String specialInstructions;
    private String status;
    private String rejectReason;
    private int isAction;
    private int isRequestQuotation;
    private int isPublic;
    private Date createdTime;
    private int isEnabled;
    private int isBided;
    private double bidAmount;
    private List<ProjectImageDto> images;
}
