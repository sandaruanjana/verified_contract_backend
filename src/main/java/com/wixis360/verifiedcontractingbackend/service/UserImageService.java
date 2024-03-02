package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.UploadUserImageDto;
import com.wixis360.verifiedcontractingbackend.dto.UserImageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface UserImageService {
    boolean save(UploadUserImageDto uploadUserImageDto) throws IOException;
    Page<UserImageDto> findAll(Pageable page, String userId, int isPublic, String search);
    boolean deleteById(String id) throws IOException;
}
