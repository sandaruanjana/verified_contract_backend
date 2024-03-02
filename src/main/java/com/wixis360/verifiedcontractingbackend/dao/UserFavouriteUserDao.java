package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.User;
import com.wixis360.verifiedcontractingbackend.model.UserFavouriteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserFavouriteUserDao {
    int save(UserFavouriteUser userFavouriteUser);
    Page<User> findAllBySortAndPage(Pageable page, String id, String search);
    Optional<UserFavouriteUser> find(UserFavouriteUser userFavouriteUser);
    int deleteById(String id);
}
