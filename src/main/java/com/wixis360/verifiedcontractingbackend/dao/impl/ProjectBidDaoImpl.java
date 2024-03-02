package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectBidDao;
import com.wixis360.verifiedcontractingbackend.model.ProjectBid;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProjectBidDaoImpl implements ProjectBidDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(ProjectBid projectBid) {
        String sql = "INSERT INTO PROJECT_BID (ID, PROJECT_ID, USER_ID, AMOUNT, CREATED_TIME) VALUES (:id, :projectId, :userId, :amount, :createdTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(projectBid));
    }

    @Override
    public Page<ProjectBid> findAllByProjectId(Pageable page, String projectId, String search) {
        String sql = "SELECT PD.*,U.NAME,U.TELEPHONE FROM PROJECT_BID PD,USER U WHERE PD.USER_ID=U.ID AND PD.PROJECT_ID=:projectId AND " +
                "(U.NAME LIKE :search OR U.TELEPHONE LIKE :search OR PD.AMOUNT LIKE :search)";

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

        List<ProjectBid> projectBids = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> {
            ProjectBid projectBid = getProjectBid(rs);
            projectBid.setUserName(rs.getString("NAME"));
            projectBid.setUserTelephone(rs.getString("TELEPHONE"));
            return projectBid;
        });
        return new PageImpl<ProjectBid>(projectBids, page, countAllByProjectId(projectId, search));
    }

    public int countAllByProjectId(String projectId, String search) {
        String sql = "SELECT COUNT(PD.ID) AS TOTAL FROM PROJECT_BID PD,USER U WHERE PD.USER_ID=U.ID AND PD.PROJECT_ID=:projectId AND " +
                "(U.NAME LIKE :search OR U.TELEPHONE LIKE :search OR PD.AMOUNT LIKE :search)";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("projectId", projectId);
        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
    }

    @Override
    public Optional<ProjectBid> findByProjectIdAndUserId(String projectId, String userId) {
        String sql = "SELECT * FROM PROJECT_BID WHERE PROJECT_ID=:projectId AND USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectId", projectId);
        mapSqlParameterSource.addValue("userId", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource,
                    (rs, rowNum) -> Optional.of(getProjectBid(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int deleteByProjectIdAndUserId(String projectId, String userId) {
        String sql = "DELETE FROM PROJECT_BID WHERE PROJECT_ID=:projectId AND USER_ID=:userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectId", projectId);
        mapSqlParameterSource.addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private ProjectBid getProjectBid(ResultSet rs) throws SQLException {
        ProjectBid projectBid = new ProjectBid();
        projectBid.setId(rs.getString("ID"));
        projectBid.setProjectId(rs.getString("PROJECT_ID"));
        projectBid.setUserId(rs.getString("USER_ID"));
        projectBid.setAmount(rs.getDouble("AMOUNT"));
        projectBid.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
        return projectBid;
    }
}
