package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.UserImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserImageDao {
    int save(UserImage userImage);
    Page<UserImage> findAll(Pageable page, String userId, int isPublic, String search);
    Optional<UserImage> findById(String id);
    int deleteById(String id);
}
