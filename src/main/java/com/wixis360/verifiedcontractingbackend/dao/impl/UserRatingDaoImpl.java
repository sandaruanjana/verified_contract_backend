package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserRatingDao;
import com.wixis360.verifiedcontractingbackend.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserRatingDaoImpl implements UserRatingDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserRating userRating) {
        String sql = "INSERT INTO USER_RATING (ID,PROJECT_ID,USER_ID,REVIEWEE_USER_ID,RATE,COMMENT,CREATED_TIME) " +
                "VALUES (:id,:projectId,:userId,:revieweeUserId,:rate,:comment,:createdTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userRating));
    }
}
