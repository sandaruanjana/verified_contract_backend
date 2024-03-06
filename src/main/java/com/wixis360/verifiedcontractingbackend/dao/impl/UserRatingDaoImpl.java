package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserRatingDao;
import com.wixis360.verifiedcontractingbackend.dto.UserRatingDto;
import com.wixis360.verifiedcontractingbackend.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@Repository
public class UserRatingDaoImpl implements UserRatingDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserRating userRating) {

        if (isExistingRecordForProject(userRating.getProjectId())){
            String updateSql = "UPDATE USER_RATING SET USER_ID = :userId, REVIEWEE_USER_ID = :revieweeUserId, " +
                    "RATE = :rate, COMMENT = :comment, CREATED_TIME = :createdTime WHERE PROJECT_ID = :projectId";

            return namedParameterJdbcTemplate.update(updateSql, new BeanPropertySqlParameterSource(userRating));
        }

        String sql = "INSERT INTO USER_RATING (ID,PROJECT_ID,USER_ID,REVIEWEE_USER_ID,RATE,COMMENT,CREATED_TIME) " +
                "VALUES (:id,:projectId,:userId,:revieweeUserId,:rate,:comment,:createdTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userRating));
    }

    @Override
    public UserRatingDto getRating(String projectId) {
        String sql = "SELECT RATE, COMMENT FROM USER_RATING WHERE PROJECT_ID = :projectId";
        Map<String, Object> params = Collections.singletonMap("projectId", projectId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<UserRatingDto>(UserRatingDto.class));
    }

    @Override
    public double getAverageRateForContractor(String contractorId) {
        String sql = "SELECT AVG(RATE) FROM USER_RATING WHERE REVIEWEE_USER_ID = :contractorId";
        Map<String, Object> params = Collections.singletonMap("contractorId", contractorId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Double.class);
    }

    public boolean isExistingRecordForProject(String projectId) {
        String sql = "SELECT COUNT(*) FROM USER_RATING WHERE PROJECT_ID = :projectId";
        Map<String, Object> params = Collections.singletonMap("projectId", projectId);
        int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count > 0;
    }
}
