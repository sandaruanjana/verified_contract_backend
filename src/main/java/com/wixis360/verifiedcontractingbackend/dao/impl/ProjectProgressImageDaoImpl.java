package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectProgressImageDao;
import com.wixis360.verifiedcontractingbackend.model.ProjectProgressImage;
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
public class ProjectProgressImageDaoImpl implements ProjectProgressImageDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(ProjectProgressImage projectProgressImage) {
        String sql = "INSERT INTO PROJECT_PROGRESS_IMAGE (ID, PROJECT_PROGRESS_ID, PROJECT_ID, NAME, DESCRIPTION, UPLOAD_TIME) " +
                "VALUES (:id, :projectProgressId, :projectId, :name, :description, :uploadTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(projectProgressImage));
    }

    @Override
    public List<ProjectProgressImage> findAllByProjectProgressId(String projectProgressId) {
        String sql = "SELECT * FROM PROJECT_PROGRESS_IMAGE WHERE PROJECT_PROGRESS_ID=:projectProgressId ORDER BY UPLOAD_TIME DESC";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectProgressId", projectProgressId);
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProjectProgressImage(rs));
    }

    private ProjectProgressImage getProjectProgressImage(ResultSet rs) throws SQLException {
        ProjectProgressImage projectProgressImage = new ProjectProgressImage();
        projectProgressImage.setId(rs.getString("ID"));
        projectProgressImage.setProjectProgressId(rs.getString("PROJECT_PROGRESS_ID"));
        projectProgressImage.setProjectId(rs.getString("PROJECT_ID"));
        projectProgressImage.setName(rs.getString("NAME"));
        projectProgressImage.setDescription(rs.getString("DESCRIPTION"));
        projectProgressImage.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
        return projectProgressImage;
    }
}
