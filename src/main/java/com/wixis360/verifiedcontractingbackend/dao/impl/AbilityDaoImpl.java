package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.AbilityDao;
import com.wixis360.verifiedcontractingbackend.model.Ability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AbilityDaoImpl implements AbilityDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Ability> findAll() {
        String sql = "SELECT * FROM ABILITY ORDER BY NAME ASC";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> getAbility(rs));
    }

    private Ability getAbility(ResultSet resultSet) throws SQLException {
        Ability ability = new Ability();
        ability.setId(resultSet.getString("ID"));
        ability.setName(resultSet.getString("NAME"));
        ability.setIsEnabled(resultSet.getInt("IS_ENABLED"));
        return ability;
    }
}
