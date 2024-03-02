package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.RequestUserPasswordDao;
import com.wixis360.verifiedcontractingbackend.model.RequestUserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class RequestUserPasswordDaoImpl implements RequestUserPasswordDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(RequestUserPassword requestUserPassword) {
        String sql = "INSERT INTO REQUEST_USER_PASSWORD (ID, USER_ID) VALUES (:id, :userId)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(requestUserPassword));
    }

    @Override
    public Optional<RequestUserPassword> findByUserId(String userId) {
        String sql = "SELECT * FROM REQUEST_USER_PASSWORD WHERE USER_ID=:userId";
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", userId),
                    (rs, rowNum) -> getRequestUserPassword(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = "DELETE FROM REQUEST_USER_PASSWORD WHERE USER_ID=:userId";
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("userId", userId));
    }

    private RequestUserPassword getRequestUserPassword(ResultSet rs) throws SQLException {
        RequestUserPassword requestUserPassword = new RequestUserPassword();
        requestUserPassword.setId(rs.getString("ID"));
        requestUserPassword.setUserId(rs.getString("USER_ID"));
        requestUserPassword.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
        return requestUserPassword;
    }
}
