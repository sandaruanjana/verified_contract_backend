package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.UserRatingDto;

public interface UserRatingService {
    boolean save(UserRatingDto userRatingDto);

    UserRatingDto getRating(String projectId);

    double getAverageRateForContractor(String contractorId);
}
