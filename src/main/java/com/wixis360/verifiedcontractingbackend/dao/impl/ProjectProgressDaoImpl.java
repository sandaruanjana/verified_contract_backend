package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectProgressDao;
import com.wixis360.verifiedcontractingbackend.model.ProjectProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProjectProgressDaoImpl implements ProjectProgressDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(ProjectProgress projectProgress) {
        String sql = "INSERT INTO PROJECT_PROGRESS (ID, PROJECT_ID, WEEK, TITLE, DESCRIPTION, CREATED_TIME) " +
                "VALUES (:id, :projectId, :week, :title, :description, :createdTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(projectProgress));
    }

    @Override
    public Page<ProjectProgress> findAll(Pageable page, String projectId, String search) {
        String sql = "SELECT * FROM PROJECT_PROGRESS WHERE PROJECT_ID=:projectId AND (WEEK LIKE :search OR TITLE LIKE :search OR DESCRIPTION LIKE :search)";

        // Add sorting
        if (page.getSort().isSorted()) {
            String sortQuery = page.getSort()
                    .stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            sql += " ORDER BY " + sortQuery;
        }

        // Add pagination
        sql += " LIMIT " + (page.getPageNumber() * page.getPageSize()) + " , " + page.getPageSize();

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("projectId", projectId);

        List<ProjectProgress> projectProgresses = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProjectProgress(rs));
        return new PageImpl<ProjectProgress>(projectProgresses, page, countAll(projectId, search));
    }

    public int countAll(String projectId, String search) {
        String sql = "SELECT COUNT(ID) AS TOTAL FROM PROJECT_PROGRESS WHERE PROJECT_ID=:projectId AND (WEEK LIKE :search OR TITLE LIKE :search OR DESCRIPTION LIKE :search)";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("projectId", projectId);
        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
    }

    @Override
    public int findLastWeekNumberByProjectId(String projectId) {
        String sql = "SELECT WEEK FROM PROJECT_PROGRESS WHERE PROJECT_ID=:projectId ORDER BY CREATED_TIME DESC LIMIT 1";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectId", projectId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("WEEK"));
        } catch (Exception e) {
            return 0;
        }
    }


    private ProjectProgress getProjectProgress(ResultSet rs) throws SQLException {
        ProjectProgress projectProgress = new ProjectProgress();
        projectProgress.setId(rs.getString("ID"));
        projectProgress.setProjectId(rs.getString("PROJECT_ID"));
        projectProgress.setWeek(rs.getInt("WEEK"));
        projectProgress.setTitle(rs.getString("TITLE"));
        projectProgress.setDescription(rs.getString("DESCRIPTION"));
        projectProgress.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
        return projectProgress;
    }
}
