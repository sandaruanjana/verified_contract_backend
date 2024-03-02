package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserFavouriteUserDao;
import com.wixis360.verifiedcontractingbackend.model.User;
import com.wixis360.verifiedcontractingbackend.model.UserFavouriteUser;
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
public class UserFavouriteUserDaoImpl implements UserFavouriteUserDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(UserFavouriteUser userFavouriteUser) {
        String sql = "INSERT INTO USER_FAVOURITE_USER (ID, USER_ID, FAVOURITE_USER_ID) VALUES (:id, :userId, :favouriteUserId)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userFavouriteUser));
    }

    @Override
    public Page<User> findAllBySortAndPage(Pageable page, String id, String search) {
        String sql = "SELECT DISTINCT U.* FROM USER U,USER_FAVOURITE_USER UFU WHERE U.ID=UFU.FAVOURITE_USER_ID AND UFU.USER_ID=:id AND " +
                "(U.NAME LIKE :search OR U.EMAIL LIKE :search OR U.TELEPHONE LIKE :search OR U.ADDRESS_LINE_1 LIKE :search OR " +
                "U.ADDRESS_LINE_2 LIKE :search OR U.ZIP_CODE LIKE :search)";

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
        mapSqlParameterSource.addValue("id", id);

        List<User> users = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getUser(rs));
        return new PageImpl<User>(users, page, countAllBySortAndPage(id, search));
    }

    public int countAllBySortAndPage(String id, String search) {
        String sql = "SELECT COUNT(DISTINCT U.ID) AS TOTAL FROM USER U,USER_FAVOURITE_USER UFU WHERE U.ID=UFU.FAVOURITE_USER_ID AND UFU.USER_ID=:id AND " +
                "(U.NAME LIKE :search OR U.EMAIL LIKE :search OR U.TELEPHONE LIKE :search OR U.ADDRESS_LINE_1 LIKE :search OR " +
                "U.ADDRESS_LINE_2 LIKE :search OR U.ZIP_CODE LIKE :search)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
    }

    @Override
    public Optional<UserFavouriteUser> find(UserFavouriteUser userFavouriteUser) {
        String sql = "SELECT * FROM USER_FAVOURITE_USER WHERE USER_ID=:userId AND FAVOURITE_USER_ID=:favouriteUserId";
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("userId", userFavouriteUser.getUserId());
            mapSqlParameterSource.addValue("favouriteUserId", userFavouriteUser.getFavouriteUserId());
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> getUserFavouriteUser(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int deleteById(String id) {
        String sql = "DELETE FROM USER_FAVOURITE_USER WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    private UserFavouriteUser getUserFavouriteUser(ResultSet rs) throws SQLException {
        UserFavouriteUser userFavouriteUser = new UserFavouriteUser();
        userFavouriteUser.setId(rs.getString("ID"));
        userFavouriteUser.setUserId(rs.getString("USER_ID"));
        userFavouriteUser.setFavouriteUserId(rs.getString("FAVOURITE_USER_ID"));
        return userFavouriteUser;
    }

    private User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("ID"));
        user.setRoleId(rs.getString("ROLE_ID"));
        user.setName(rs.getString("NAME"));
        user.setProfilePicture(rs.getString("PROFILE_PICTURE"));
        user.setEmail(rs.getString("EMAIL"));
        user.setPassword(rs.getString("PASSWORD"));
        user.setTelephone(rs.getString("TELEPHONE"));
        user.setAddressLine1(rs.getString("ADDRESS_LINE_1"));
        user.setAddressLine2(rs.getString("ADDRESS_LINE_2"));
        user.setZipCode(rs.getString("ZIP_CODE"));
        user.setSmallInfo(rs.getString("SMALL_INFO"));
        user.setBio(rs.getString("BIO"));
        user.setFacebookUrl(rs.getString("FACEBOOK_URL"));
        user.setTwitterUrl(rs.getString("TWITTER_URL"));
        user.setLinkedInUrl(rs.getString("LINKEDIN_URL"));
        user.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
        user.setIsEnabled(rs.getInt("IS_ENABLED"));
        return user;
    }
}
