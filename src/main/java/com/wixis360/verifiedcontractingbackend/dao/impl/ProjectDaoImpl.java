package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectDao;
import com.wixis360.verifiedcontractingbackend.enums.ProjectStatus;
import com.wixis360.verifiedcontractingbackend.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProjectDaoImpl implements ProjectDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(Project project) {
        String sql = "INSERT INTO PROJECT (ID, USER_ID, ASSIGN_USER_ID, NAME, PREFERRED_DATE, ADDRESS_LINE_1, ADDRESS_LINE_2, " +
                "NATURE, ZIP_CODE, LONGITUDE, LATITUDE, CATEGORY, CATEGORY_ONE_ID, CATEGORY_TWO_ID, CATEGORY_THREE_ID, " +
                "SPECIAL_INSTRUCTIONS, STATUS, IS_ACTION, IS_REQUEST_QUOTATION, IS_PUBLIC, CREATED_TIME) VALUES (:id, :userId, :assignUserId, " +
                ":name, :preferredDate, :addressLine1, :addressLine2, :nature, :zipCode, :longitude, :latitude, :category, " +
                ":categoryOneId, :categoryTwoId, :categoryThreeId, :specialInstructions, :status, :isAction, :isRequestQuotation, :isPublic, :createdTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(project));
    }

    @Override
    public Page<Project> findAll(Pageable page, String userId, int isPublic, int radius, String search, String category, String categoryOneId, String categoryTwoId, String categoryThreeId) {
        /*String sql = "SELECT DISTINCT P.*,MIN(ST_DISTANCE_SPHERE(POINT(P.LONGITUDE, P.LATITUDE), POINT(Z.USER_LONGITUDE, " +
                "Z.USER_LATITUDE))/1000) AS DISTANCE FROM PROJECT P CROSS JOIN (SELECT LATITUDE AS USER_LATITUDE, " +
                "LONGITUDE AS USER_LONGITUDE FROM USER_PREFERRED_ZIP_CODE WHERE USER_ID = :userId ) Z WHERE P.IS_PUBLIC = :isPublic AND " +
                "(P.CATEGORY_ONE_ID='f4e49bba-3299-11ee-80f5-705a0f2e41ff') AND (P.PREFERRED_DATE LIKE :search OR P.NAME LIKE :search OR " +
                "P.SPECIAL_INSTRUCTIONS LIKE :search OR P.ADDRESS_LINE_1 LIKE :search OR P.ADDRESS_LINE_2 LIKE :search OR P.CATEGORY LIKE :search) AND " +
                "ST_DISTANCE_SPHERE(POINT(P.LONGITUDE,P.LATITUDE), POINT(Z.USER_LONGITUDE,Z.USER_LATITUDE))/1000 <= :radius " +
                "GROUP BY P.ID ORDER BY DISTANCE LIMIT :page, :size";*/

        String sql = "SELECT DISTINCT P.*,MIN(ST_DISTANCE_SPHERE(POINT(P.LONGITUDE, P.LATITUDE), POINT(Z.USER_LONGITUDE, " +
                "Z.USER_LATITUDE))/1000) AS DISTANCE FROM PROJECT P CROSS JOIN (SELECT LATITUDE AS USER_LATITUDE, " +
                "LONGITUDE AS USER_LONGITUDE FROM USER_PREFERRED_ZIP_CODE WHERE USER_ID = :userId ) Z " +
                "WHERE P.IS_PUBLIC = :isPublic AND P.STATUS='INITIATE' AND ";

        if (!category.isBlank()) {
            sql += "P.CATEGORY = :category AND ";
        }

        if (!categoryOneId.isBlank()) {
            sql += "P.CATEGORY_ONE_ID = :categoryOneId AND ";
        }

        if (!categoryTwoId.isBlank()) {
            sql += "P.CATEGORY_TWO_ID = :categoryTwoId AND ";
        }

        if (!categoryThreeId.isBlank()) {
            sql += "P.CATEGORY_THREE_ID = :categoryThreeId AND ";
        }

        sql += "(P.PREFERRED_DATE LIKE :search OR P.NAME LIKE :search OR P.SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "P.ADDRESS_LINE_1 LIKE :search OR P.ADDRESS_LINE_2 LIKE :search OR P.CATEGORY LIKE :search) AND " +
                "ST_DISTANCE_SPHERE(POINT(P.LONGITUDE,P.LATITUDE), POINT(Z.USER_LONGITUDE,Z.USER_LATITUDE))/1000 <= :radius " +
                "GROUP BY P.ID ORDER BY DISTANCE";

        // Add pagination
        sql += " LIMIT " + (page.getPageNumber() * page.getPageSize()) + " , " + page.getPageSize();

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        mapSqlParameterSource.addValue("isPublic", isPublic);
        mapSqlParameterSource.addValue("radius", radius);
        mapSqlParameterSource.addValue("search", "%" + search + "%");

        if (!category.isBlank()) {
            mapSqlParameterSource.addValue("category", category);
        }

        if (!categoryOneId.isBlank()) {
            mapSqlParameterSource.addValue("categoryOneId", categoryOneId);
        }

        if (!categoryTwoId.isBlank()) {
            mapSqlParameterSource.addValue("categoryTwoId", categoryTwoId);
        }

        if (!categoryThreeId.isBlank()) {
            mapSqlParameterSource.addValue("categoryThreeId", categoryThreeId);
        }

        List<Project> projects = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProject(rs));
        return new PageImpl<Project>(projects, page, countAll(userId, isPublic, radius, search, categoryOneId, categoryTwoId, categoryThreeId));
    }

    public int countAll(String userId, int isPublic, int radius, String search, String categoryOneId, String categoryTwoId, String categoryThreeId) {
        String sql = "SELECT COUNT(DISTINCT P.ID) AS TOTAL FROM PROJECT P CROSS JOIN (SELECT LATITUDE AS USER_LATITUDE, " +
                "LONGITUDE AS USER_LONGITUDE FROM USER_PREFERRED_ZIP_CODE WHERE USER_ID = :userId ) Z " +
                "WHERE P.IS_PUBLIC = :isPublic AND P.STATUS='INITIATE' AND ";

        if (!categoryOneId.isBlank()) {
            sql += "P.CATEGORY_ONE_ID = :categoryOneId AND ";
        }

        if (!categoryTwoId.isBlank()) {
            sql += "P.CATEGORY_TWO_ID = :categoryTwoId AND ";
        }

        if (!categoryThreeId.isBlank()) {
            sql += "P.CATEGORY_THREE_ID = :categoryThreeId AND ";
        }

        sql += "(P.PREFERRED_DATE LIKE :search OR P.NAME LIKE :search OR P.SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "P.ADDRESS_LINE_1 LIKE :search OR P.ADDRESS_LINE_2 LIKE :search OR P.CATEGORY LIKE :search) AND " +
                "ST_DISTANCE_SPHERE(POINT(P.LONGITUDE,P.LATITUDE), POINT(Z.USER_LONGITUDE,Z.USER_LATITUDE))/1000 <= :radius " +
                "GROUP BY P.ID";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("userId", userId);
        mapSqlParameterSource.addValue("isPublic", isPublic);
        mapSqlParameterSource.addValue("radius", radius);
        mapSqlParameterSource.addValue("search", "%" + search + "%");

        if (!categoryOneId.isBlank()) {
            mapSqlParameterSource.addValue("categoryOneId", categoryOneId);
        }

        if (!categoryTwoId.isBlank()) {
            mapSqlParameterSource.addValue("categoryTwoId", categoryTwoId);
        }

        if (!categoryThreeId.isBlank()) {
            mapSqlParameterSource.addValue("categoryThreeId", categoryThreeId);
        }

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Page<Project> findAllByAssignUserId(Pageable page, String assignUserId, String status, String search) {
        String sql = "SELECT * FROM PROJECT WHERE ASSIGN_USER_ID=:assignUserId AND STATUS=:status AND " +
                "(PREFERRED_DATE LIKE :search OR NAME LIKE :search OR SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "ADDRESS_LINE_1 LIKE :search OR ADDRESS_LINE_2 LIKE :search OR CATEGORY LIKE :search)";

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
        mapSqlParameterSource.addValue("assignUserId", assignUserId);
        mapSqlParameterSource.addValue("status", status);

        List<Project> projects = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProject(rs));
        return new PageImpl<Project>(projects, page, countAllByAssignUserId(assignUserId, status, search));
    }

    public int countAllByAssignUserId(String assignUserId, String status, String search) {
        String sql = "SELECT COUNT(ID) AS TOTAL FROM PROJECT WHERE ASSIGN_USER_ID=:assignUserId AND STATUS=:status AND " +
                "(PREFERRED_DATE LIKE :search OR NAME LIKE :search OR SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "ADDRESS_LINE_1 LIKE :search OR ADDRESS_LINE_2 LIKE :search OR CATEGORY LIKE :search)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("assignUserId", assignUserId);
        mapSqlParameterSource.addValue("status", status);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Page<Project> findAllPrivateByAssignUserId(Pageable page, String assignUserId, String status, String search) {
        String sql = "SELECT * FROM PROJECT WHERE ASSIGN_USER_ID=:assignUserId AND IS_PUBLIC=0 AND STATUS=:status AND " +
                "(PREFERRED_DATE LIKE :search OR NAME LIKE :search OR SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "ADDRESS_LINE_1 LIKE :search OR ADDRESS_LINE_2 LIKE :search OR CATEGORY LIKE :search)";

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
        mapSqlParameterSource.addValue("assignUserId", assignUserId);
        mapSqlParameterSource.addValue("status", status);

        List<Project> projects = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProject(rs));
        return new PageImpl<Project>(projects, page, countAllPrivateByAssignUserId(assignUserId, status, search));
    }

    public int countAllPrivateByAssignUserId(String assignUserId, String status, String search) {
        String sql = "SELECT COUNT(ID) AS TOTAL FROM PROJECT WHERE ASSIGN_USER_ID=:assignUserId AND IS_PUBLIC=0 AND STATUS=:status AND " +
                "(PREFERRED_DATE LIKE :search OR NAME LIKE :search OR SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "ADDRESS_LINE_1 LIKE :search OR ADDRESS_LINE_2 LIKE :search OR CATEGORY LIKE :search)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("assignUserId", assignUserId);
        mapSqlParameterSource.addValue("status", status);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Page<Project> findAllByUserId(Pageable page, String userId, String status, String search) {
        String sql = "SELECT * FROM PROJECT WHERE USER_ID=:userId AND STATUS=:status AND " +
                "(PREFERRED_DATE LIKE :search OR NAME LIKE :search OR SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "ADDRESS_LINE_1 LIKE :search OR ADDRESS_LINE_2 LIKE :search OR CATEGORY LIKE :search)";

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
        mapSqlParameterSource.addValue("userId", userId);
        mapSqlParameterSource.addValue("status", status);

        List<Project> projects = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getProject(rs));
        return new PageImpl<Project>(projects, page, countAllByUserId(userId, status, search));
    }

    public int countAllByUserId(String userId, String status, String search) {
        String sql = "SELECT COUNT(ID) AS TOTAL FROM PROJECT WHERE USER_ID=:userId AND STATUS=:status AND " +
                "(PREFERRED_DATE LIKE :search OR NAME LIKE :search OR SPECIAL_INSTRUCTIONS LIKE :search OR " +
                "ADDRESS_LINE_1 LIKE :search OR ADDRESS_LINE_2 LIKE :search OR CATEGORY LIKE :search)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("userId", userId);
        mapSqlParameterSource.addValue("status", status);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Optional<Project> findById(String id) {
        String sql = "SELECT * FROM PROJECT WHERE ID=:id";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id),
                    (rs, rowNum) -> Optional.of(getProject(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int updateStatus(String id, String status) {
        String sql = "UPDATE PROJECT SET STATUS=:status WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("status", status);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    @Override
    public int updateStatusAndAssignUser(String id, String status, String assignUserId) {
        String sql = "UPDATE PROJECT SET STATUS=:status,ASSIGN_USER_ID=:assignUserId WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("status", status);
        mapSqlParameterSource.addValue("assignUserId", assignUserId);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    @Override
    public int updateStatusAndRejectReason(String id, String status, String rejectReason) {
        String sql = "UPDATE PROJECT SET STATUS=:status,REJECT_REASON=:rejectReason WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("status", status);
        mapSqlParameterSource.addValue("rejectReason", rejectReason);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    @Override
    public int delete(String id) {

        if (!projectExists(id)) {
            throw new RuntimeException("Invalid Project ID");
        }

        String sql = "UPDATE PROJECT SET STATUS=:status WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("status", ProjectStatus.DELETED.name());
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private boolean projectExists(String id) {
        String checkSql = "SELECT COUNT(*) FROM PROJECT WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);

        try {
            Integer count = namedParameterJdbcTemplate.queryForObject(checkSql, mapSqlParameterSource, Integer.class);
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error checking project existence for ID " + id, ex);
        }
    }

    private Project getProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getString("ID"));
        project.setUserId(rs.getString("USER_ID"));
        project.setAssignUserId(rs.getString("ASSIGN_USER_ID"));
        project.setName(rs.getString("NAME"));
        project.setPreferredDate(rs.getDate("PREFERRED_DATE"));
        project.setAddressLine1(rs.getString("ADDRESS_LINE_1"));
        project.setAddressLine2(rs.getString("ADDRESS_LINE_2"));
        project.setNature(rs.getString("NATURE"));
        project.setZipCode(rs.getString("ZIP_CODE"));
        project.setLongitude(rs.getString("LONGITUDE"));
        project.setLatitude(rs.getString("LATITUDE"));
        project.setCategory(rs.getString("CATEGORY"));
        project.setCategoryOneId(rs.getString("CATEGORY_ONE_ID"));
        project.setCategoryTwoId(rs.getString("CATEGORY_TWO_ID"));
        project.setCategoryThreeId(rs.getString("CATEGORY_THREE_ID"));
        project.setSpecialInstructions(rs.getString("SPECIAL_INSTRUCTIONS"));
        project.setStatus(rs.getString("STATUS"));
        project.setRejectReason(rs.getString("REJECT_REASON"));
        project.setIsAction(rs.getInt("IS_ACTION"));
        project.setIsRequestQuotation(rs.getInt("IS_REQUEST_QUOTATION"));
        project.setIsPublic(rs.getInt("IS_PUBLIC"));
        project.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
        project.setIsEnabled(rs.getInt("IS_ENABLED"));
        return project;
    }
}
