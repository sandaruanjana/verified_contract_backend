package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserRatingDao;
import com.wixis360.verifiedcontractingbackend.dto.UserRatingDto;
import com.wixis360.verifiedcontractingbackend.model.UserRating;
import com.wixis360.verifiedcontractingbackend.service.UserRatingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Transactional
@Service
@AllArgsConstructor
public class UserRatingServiceImpl implements UserRatingService {
    private UserRatingDao userRatingDao;
    private ModelMapper mapper;

    @Override
    public boolean save(UserRatingDto userRatingDto) {
        userRatingDto.setId(UUID.randomUUID().toString().replace("-",""));
        userRatingDto.setCreatedTime(new Date());
        return userRatingDao.save(getUserRating(userRatingDto)) > 0;
    }

    @Override
    public UserRatingDto getRating(String projectId) {
        return userRatingDao.getRating(projectId);
    }

    @Override
    public double getAverageRateForContractor(String contractorId) {

        double rating = 0.0;

        try {
           rating = userRatingDao.getAverageRateForContractor(contractorId);
        }catch (Exception e){
            e.printStackTrace();
        }


        return Math.round(rating * 100.0) / 100.0;
    }

    private UserRating getUserRating(UserRatingDto userRatingDto) {
        return mapper.map(userRatingDto, UserRating.class);
    }
}
