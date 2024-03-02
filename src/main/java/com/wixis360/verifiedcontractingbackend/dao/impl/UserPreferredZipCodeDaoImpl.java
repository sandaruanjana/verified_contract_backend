package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserPreferredZipCodeDao;
import com.wixis360.verifiedcontractingbackend.model.UserPreferredZipCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserPreferredZipCodeDaoImpl implements UserPreferredZipCodeDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserPreferredZipCode userPreferredZipCode) {
        String sql = "INSERT INTO USER_PREFERRED_ZIP_CODE (ID,USER_ID,ZIP_CODE,LONGITUDE,LATITUDE) VALUES (:id,:userId,:zipCode,:longitude,:latitude)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userPreferredZipCode));
    }

    @Override
    public List<UserPreferredZipCode> findAllByUserId(String userId) {
        String sql = "SELECT * FROM USER_PREFERRED_ZIP_CODE WHERE USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getUserPreferredZipCode(rs));
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = "DELETE FROM USER_PREFERRED_ZIP_CODE WHERE USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private UserPreferredZipCode getUserPreferredZipCode(ResultSet resultSet) throws SQLException {
        UserPreferredZipCode userPreferredZipCode = new UserPreferredZipCode();
        userPreferredZipCode.setId(resultSet.getString("ID"));
        userPreferredZipCode.setUserId(resultSet.getString("USER_ID"));
        userPreferredZipCode.setZipCode(resultSet.getString("ZIP_CODE"));
        userPreferredZipCode.setLongitude(resultSet.getString("LONGITUDE"));
        userPreferredZipCode.setLatitude(resultSet.getString("LATITUDE"));
        return userPreferredZipCode;
    }
}
