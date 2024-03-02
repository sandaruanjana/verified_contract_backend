package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserSkillDao;
import com.wixis360.verifiedcontractingbackend.model.UserSkill;
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
public class UserSkillDaoImpl implements UserSkillDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserSkill userSkill) {
        String sql = "INSERT INTO USER_SKILL (ID, USER_ID, SKILL_ID) VALUES (:id, :userId, :skillId)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userSkill));
    }

    @Override
    public List<UserSkill> findAllByUserId(String userId) {
        String sql = "SELECT * FROM USER_SKILL WHERE USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getUserSkill(rs));
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = "DELETE FROM USER_SKILL WHERE USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private UserSkill getUserSkill(ResultSet resultSet) throws SQLException {
        UserSkill userSkill = new UserSkill();
        userSkill.setId(resultSet.getString("ID"));
        userSkill.setUserId(resultSet.getString("USER_ID"));
        userSkill.setSkillId(resultSet.getString("SKILL_ID"));
        return userSkill;
    }
}
