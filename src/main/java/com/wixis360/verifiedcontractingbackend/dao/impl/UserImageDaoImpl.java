package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserImageDao;
import com.wixis360.verifiedcontractingbackend.model.User;
import com.wixis360.verifiedcontractingbackend.model.UserImage;
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
public class UserImageDaoImpl implements UserImageDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserImage userImage) {
        String sql = "INSERT INTO USER_IMAGE (ID, USER_ID, PROJECT_IMAGE_ID, NAME, DESCRIPTION, IS_PUBLIC, UPLOAD_TIME) " +
                "VALUES (:id, :userId, :projectImageId, :name, :description, :isPublic, :uploadTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userImage));
    }

    @Override
    public Page<UserImage> findAll(Pageable page, String userId, int isPublic, String search) {
        String sql = "SELECT * FROM USER_IMAGE WHERE USER_ID=:userId AND IS_PUBLIC=:isPublic AND DESCRIPTION LIKE :search";

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
        mapSqlParameterSource.addValue("isPublic", isPublic);

        List<UserImage> userImages = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getUserImage(rs));
        return new PageImpl<UserImage>(userImages, page, countAll(userId, isPublic, search));
    }

    public int countAll(String userId, int isPublic, String search) {
        String sql = "SELECT COUNT(ID) AS TOTAL FROM USER_IMAGE WHERE USER_ID=:userId AND IS_PUBLIC=:isPublic AND DESCRIPTION LIKE :search";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("userId", userId);
        mapSqlParameterSource.addValue("isPublic", isPublic);
        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
    }

    @Override
    public Optional<UserImage> findById(String id) {
        String sql = "SELECT * FROM USER_IMAGE WHERE ID=:id";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), (rs, rowNum) -> Optional.of(getUserImage(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int deleteById(String id) {
        String sql = "DELETE FROM USER_IMAGE WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private UserImage getUserImage(ResultSet rs) throws SQLException {
        UserImage userImage = new UserImage();
        userImage.setId(rs.getString("ID"));
        userImage.setUserId(rs.getString("USER_ID"));
        userImage.setProjectImageId(rs.getString("PROJECT_IMAGE_ID"));
        userImage.setName(rs.getString("NAME"));
        userImage.setDescription(rs.getString("DESCRIPTION"));
        userImage.setIsPublic(rs.getInt("IS_PUBLIC"));
        userImage.setUploadTime(rs.getTimestamp("UPLOAD_TIME"));
        return userImage;
    }
}
