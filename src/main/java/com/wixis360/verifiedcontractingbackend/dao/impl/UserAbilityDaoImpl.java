package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserAbilityDao;
import com.wixis360.verifiedcontractingbackend.model.UserAbility;
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
public class UserAbilityDaoImpl implements UserAbilityDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserAbility userAbility) {
        String sql = "INSERT INTO USER_ABILITY (ID, USER_ID, ABILITY_ID) VALUES (:id, :userId, :abilityId)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userAbility));
    }

    @Override
    public List<UserAbility> findAllByUserId(String userId) {
        String sql = "SELECT * FROM USER_ABILITY WHERE USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getUserAbility(rs));
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = "DELETE FROM USER_ABILITY WHERE USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private UserAbility getUserAbility(ResultSet resultSet) throws SQLException {
        UserAbility userAbility = new UserAbility();
        userAbility.setId(resultSet.getString("ID"));
        userAbility.setUserId(resultSet.getString("USER_ID"));
        userAbility.setAbilityId(resultSet.getString("ABILITY_ID"));
        return userAbility;
    }
}
