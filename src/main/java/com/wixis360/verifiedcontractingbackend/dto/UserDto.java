package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private String id;
    private String roleId;
    private String name;
    private String profilePicture;
    private String email;
    private String password;
    private String telephone;
    private String addressLine1;
    private String addressLine2;
    private String zipCode;
    private String longitude;
    private String latitude;
    private String smallInfo;
    private String bio;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedInUrl;
    private String role;
    private Date createdTime;
    private int isEnabled;
    private int isFavourite;
    private List<UserPreferredZipCodeDto> preferredZipCodes = new ArrayList<>();
    private List<String> abilities = new ArrayList<>();
    private List<String> skills = new ArrayList<>();
}
