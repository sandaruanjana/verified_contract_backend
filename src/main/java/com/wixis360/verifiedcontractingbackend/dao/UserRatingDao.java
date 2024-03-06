package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.dto.UserRatingDto;
import com.wixis360.verifiedcontractingbackend.model.UserRating;

public interface UserRatingDao {
    int save(UserRating userRating);

    UserRatingDto getRating(String projectId);

    double getAverageRateForContractor(String contractorId);
}
