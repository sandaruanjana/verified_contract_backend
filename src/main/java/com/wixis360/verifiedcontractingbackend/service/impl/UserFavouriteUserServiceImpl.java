package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserFavouriteUserDao;
import com.wixis360.verifiedcontractingbackend.dto.UserDto;
import com.wixis360.verifiedcontractingbackend.dto.UserFavouriteUserDto;
import com.wixis360.verifiedcontractingbackend.model.User;
import com.wixis360.verifiedcontractingbackend.model.UserFavouriteUser;
import com.wixis360.verifiedcontractingbackend.service.UserFavouriteUserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@AllArgsConstructor
public class UserFavouriteUserServiceImpl implements UserFavouriteUserService {
    private UserFavouriteUserDao userFavouriteUserDao;
    private ModelMapper mapper;

    @Override
    public boolean save(UserFavouriteUserDto userFavouriteUserDto) {
        userFavouriteUserDto.setId(UUID.randomUUID().toString());

        Optional<UserFavouriteUser> optionalUserFavouriteUser = userFavouriteUserDao.find(getUserFavouriteUser(userFavouriteUserDto));

        if (optionalUserFavouriteUser.isPresent()) {
            UserFavouriteUser userFavouriteUser = optionalUserFavouriteUser.get();
            return userFavouriteUserDao.deleteById(userFavouriteUser.getId()) > 0;
        } else {
            return userFavouriteUserDao.save(getUserFavouriteUser(userFavouriteUserDto)) > 0;
        }
    }

    @Override
    public Page<UserDto> findAllBySortAndPage(Pageable page, String id, String search) {
        Page<User> userPage = userFavouriteUserDao.findAllBySortAndPage(page, id, search);
        List<UserDto> customerDtoList = StreamSupport.stream(userPage.spliterator(), false)
                .map(this::getUserDto)
                .collect(Collectors.toList());
        return new PageImpl<>(customerDtoList, page, userPage.getTotalElements());
    }

    private UserFavouriteUser getUserFavouriteUser(UserFavouriteUserDto userFavouriteUserDto) {
        return mapper.map(userFavouriteUserDto, UserFavouriteUser.class);
    }

    private UserDto getUserDto(User user) {
        return mapper.map(user, UserDto.class);
    }
}
