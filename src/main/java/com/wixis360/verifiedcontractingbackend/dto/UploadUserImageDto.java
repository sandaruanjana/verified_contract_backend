package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadUserImageDto {
    private String userId;
    private MultipartFile imageFile;
    private String description;
    private int isPublic;
}
