package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectImageDao;
import com.wixis360.verifiedcontractingbackend.model.ProjectImage;
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
public class ProjectImageDaoImpl implements ProjectImageDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(ProjectImage projectImage) {
        String sql = "INSERT INTO PROJECT_IMAGE (ID, PROJECT_ID, NAME, DESCRIPTION, UPLOAD_TIME) VALUES " +
                "(:id, :projectId, :name, :description, :uploadTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(projectImage));
    }

    @Override
    public List<ProjectImage> findAllByProjectId(String projectId) {
        String sql = "SELECT * FROM PROJECT_IMAGE WHERE PROJECT_ID=:projectId ORDER BY UPLOAD_TIME DESC";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectId", projectId);
        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProjectImage(rs));
    }

    private ProjectImage getProjectImage(ResultSet rs) throws SQLException {
        ProjectImage projectImage = new ProjectImage();
        projectImage.setId(rs.getString("ID"));
        projectImage.setProjectId(rs.getString("PROJECT_ID"));
        projectImage.setName(rs.getString("NAME"));
        projectImage.setDescription(rs.getString("DESCRIPTION"));
        projectImage.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
        return projectImage;
    }
}
