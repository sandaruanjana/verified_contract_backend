package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

@Data
public class UpdateUserPasswordDto {
    private String id;
    private String currentPassword;
    private String password;
}
