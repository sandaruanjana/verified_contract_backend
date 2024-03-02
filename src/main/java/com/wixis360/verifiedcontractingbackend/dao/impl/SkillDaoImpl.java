package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.SkillDao;
import com.wixis360.verifiedcontractingbackend.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SkillDaoImpl implements SkillDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Skill> findAllByType(String type) {
        String sql = "SELECT * FROM SKILL WHERE TYPE=:type ORDER BY NAME ASC";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("type", type);
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getSkill(rs));
    }

    private Skill getSkill(ResultSet resultSet) throws SQLException {
        Skill skill = new Skill();
        skill.setId(resultSet.getString("ID"));
        skill.setType(resultSet.getString("TYPE"));
        skill.setName(resultSet.getString("NAME"));
        skill.setIsEnabled(resultSet.getInt("IS_ENABLED"));
        return skill;
    }
}
