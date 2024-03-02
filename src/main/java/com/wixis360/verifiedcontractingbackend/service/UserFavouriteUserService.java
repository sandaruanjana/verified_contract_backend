package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.UserDto;
import com.wixis360.verifiedcontractingbackend.dto.UserFavouriteUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserFavouriteUserService {
    boolean save(UserFavouriteUserDto userFavouriteUserDto);
    Page<UserDto> findAllBySortAndPage(Pageable page, String id, String search);
}
