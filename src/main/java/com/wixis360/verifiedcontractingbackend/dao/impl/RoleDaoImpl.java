package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.RoleDao;
import com.wixis360.verifiedcontractingbackend.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleDaoImpl implements RoleDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM ROLE ORDER BY NAME ASC";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> getRole(rs));
    }

    @Override
    public Optional<Role> findById(String id) {
        String sql = "SELECT * FROM ROLE WHERE ID=:id";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id),
                    (rs, rowNum) -> Optional.of(getRole(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM ROLE WHERE NAME=:name";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("name", name),
                    (rs, rowNum) -> Optional.of(getRole(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Role getRole(ResultSet resultSet) throws SQLException {
        Role role = new Role();
        role.setId(resultSet.getString("ID"));
        role.setName(resultSet.getString("NAME"));
        role.setIsEnabled(resultSet.getInt("IS_ENABLED"));
        return role;
    }
}
