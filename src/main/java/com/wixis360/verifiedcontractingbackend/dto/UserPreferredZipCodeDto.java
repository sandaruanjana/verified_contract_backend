package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

@Data
public class UserPreferredZipCodeDto {
    private String id;
    private String userId;
    private String zipCode;
    private String longitude;
    private String latitude;
}
