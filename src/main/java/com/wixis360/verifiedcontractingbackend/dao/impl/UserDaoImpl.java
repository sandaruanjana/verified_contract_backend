package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserDao;
import com.wixis360.verifiedcontractingbackend.model.User;
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
public class UserDaoImpl implements UserDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public int save(User user) {
        String sql = "INSERT INTO USER (ID,ROLE_ID,NAME,EMAIL,PASSWORD,TELEPHONE,ADDRESS_LINE_1,ADDRESS_LINE_2,ZIP_CODE," +
                "LONGITUDE,LATITUDE,CREATED_TIME) VALUES(:id,:roleId,:name,:email,:password,:telephone,:addressLine1," +
                ":addressLine2,:zipCode,:longitude,:latitude,:createdTime)";
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(user));
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM USER WHERE ID=:id";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id),
                    (rs, rowNum) -> Optional.of(getUser(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM USER WHERE EMAIL=:email AND IS_ENABLED='1'";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("email", email),
                    (rs, rowNum) -> Optional.of(getUser(rs)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USER ORDER BY NAME ASC";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> getUser(rs));
    }

    @Override
    public Page<User> findAllBySortAndPage(Pageable page, String role, String search, String abilities, String skills) {
        String sql = "SELECT DISTINCT U.* FROM USER U LEFT JOIN USER_ABILITY UA ON U.ID=UA.USER_ID LEFT JOIN USER_SKILL US ON U.ID=US.USER_ID,ROLE R " +
                "WHERE U.ROLE_ID=R.ID AND R.NAME=:role AND (U.NAME LIKE :search OR U.EMAIL LIKE :search OR U.TELEPHONE LIKE :search OR U.ADDRESS_LINE_1 LIKE :search OR " +
                "U.ADDRESS_LINE_2 LIKE :search OR U.ZIP_CODE LIKE :search)";

        if (!abilities.isEmpty()) {
            sql += " OR UA.ABILITY_ID = :abilities ";
        }

        if (!skills.isEmpty()) {
            if (skills.split(",").length > 1) {
                String newSkills = "";

                for (int i = 0; i < skills.split(",").length; i++) {
                    if (i == skills.split(",").length - 1) {
                        newSkills += "'" + skills.split(",")[i] + "'";
                    } else {
                        newSkills += "'" + skills.split(",")[i] + "',";
                    }
                }

                sql += " OR US.SKILL_ID IN (" + newSkills + ")";
            } else {
                sql += " OR US.SKILL_ID = '" + skills + "'";
            }
        }

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
        mapSqlParameterSource.addValue("role", role);

        if (!abilities.isEmpty()) {
            mapSqlParameterSource.addValue("abilities", abilities);
        }

        List<User> users = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> getUser(rs));
        return new PageImpl<User>(users, page, countAllBySortAndPage(role, search, abilities, skills));
    }

    public int countAllBySortAndPage(String role, String search, String abilities, String skills) {
        String sql = "SELECT COUNT(DISTINCT U.ID) AS TOTAL FROM USER U LEFT JOIN USER_ABILITY UA ON U.ID=UA.USER_ID LEFT JOIN USER_SKILL US ON U.ID=US.USER_ID,ROLE R " +
                "WHERE U.ROLE_ID=R.ID AND R.NAME=:role AND (U.NAME LIKE :search OR U.EMAIL LIKE :search OR U.TELEPHONE LIKE :search OR U.ADDRESS_LINE_1 LIKE :search OR " +
                "U.ADDRESS_LINE_2 LIKE :search OR U.ZIP_CODE LIKE :search)";

        if (!abilities.isEmpty()) {
            sql += " OR UA.ABILITY_ID = :abilities ";
        }

        if (!skills.isEmpty()) {
            if (skills.split(",").length > 1) {
                String newSkills = "";

                for (int i = 0; i < skills.split(",").length; i++) {
                    if (i == skills.split(",").length - 1) {
                        newSkills += "'" + skills.split(",")[i] + "'";
                    } else {
                        newSkills += "'" + skills.split(",")[i] + "',";
                    }
                }

                sql += " OR US.SKILL_ID IN (" + newSkills + ")";
            } else {
                sql += " OR US.SKILL_ID = '" + skills + "'";
            }
        }

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("role", role);

        if (!abilities.isEmpty()) {
            mapSqlParameterSource.addValue("abilities", abilities);
        }

        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
    }

    @Override
    public Page<User> findAllByDistanceRange(Pageable page, String longitude, String latitude, int distance, String role, String search, String abilities, String skills) {
        String sql = "SELECT DISTINCT X.ID,X.ROLE_ID,X.NAME,X.PROFILE_PICTURE,X.EMAIL,X.TELEPHONE FROM (SELECT DISTINCT U.ID,U.NAME,U.PROFILE_PICTURE,U.EMAIL,U.TELEPHONE," +
                "U.ROLE_ID,ST_DISTANCE_SPHERE(POINT(:longitude, :latitude), POINT(UPZC.`LONGITUDE`, UPZC.`LATITUDE`)) / 1000 AS DISTANCE " +
                "FROM USER U LEFT JOIN USER_ABILITY UA ON U.ID=UA.USER_ID LEFT JOIN USER_SKILL US ON U.ID=US.USER_ID,ROLE R,USER_PREFERRED_ZIP_CODE UPZC " +
                "WHERE U.ROLE_ID=R.ID AND U.ID=UPZC.USER_ID AND R.NAME=:role AND (U.NAME LIKE :search OR U.EMAIL LIKE :search OR " +
                "U.TELEPHONE LIKE :search OR U.ADDRESS_LINE_1 LIKE :search OR U.ADDRESS_LINE_2 LIKE :search OR U.ZIP_CODE LIKE :search)";

        if (!abilities.isEmpty()) {
            sql += " OR UA.ABILITY_ID = :abilities ";
        }

        if (!skills.isEmpty()) {
            if (skills.split(",").length > 1) {
                String newSkills = "";

                for (int i = 0; i < skills.split(",").length; i++) {
                    if (i == skills.split(",").length - 1) {
                        newSkills += "'" + skills.split(",")[i] + "'";
                    } else {
                        newSkills += "'" + skills.split(",")[i] + "',";
                    }
                }

                sql += " OR US.SKILL_ID IN (" + newSkills + ")";
            } else {
                sql += " OR US.SKILL_ID = '" + skills + "'";
            }
        }

        sql += " HAVING DISTANCE <= :distance";

        // Add sorting
        if (page.getSort().isSorted()) {
            String sortQuery = page.getSort()
                    .stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            sql += " ORDER BY " + sortQuery;
        }

        // Add pagination
        sql += " LIMIT " + (page.getPageNumber() * page.getPageSize()) + " , " + page.getPageSize() + ")x";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("longitude", longitude);
        mapSqlParameterSource.addValue("latitude", latitude);
        mapSqlParameterSource.addValue("distance", distance);
        mapSqlParameterSource.addValue("role", role);

        if (!abilities.isEmpty()) {
            mapSqlParameterSource.addValue("abilities", abilities);
        }

        List<User> users = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getString("ID"));
            user.setRoleId(rs.getString("ROLE_ID"));
            user.setName(rs.getString("NAME"));
            user.setProfilePicture(rs.getString("PROFILE_PICTURE"));
            user.setEmail(rs.getString("EMAIL"));
            user.setTelephone(rs.getString("TELEPHONE"));
            return user;
        });

        return new PageImpl<User>(users, page, countAllByDistanceRange(longitude, latitude, distance, role, search, abilities, skills));
    }

    public int countAllByDistanceRange(String longitude, String latitude, int distance, String role, String search, String abilities, String skills) {
        String sql = "SELECT COUNT(x.TOTAL) AS TOTAL FROM (SELECT DISTINCT U.ID AS TOTAL,ST_DISTANCE_SPHERE(POINT(:longitude, :latitude), POINT(UPZC.`LONGITUDE`, UPZC.`LATITUDE`)) / 1000 AS DISTANCE " +
                "FROM USER U LEFT JOIN USER_ABILITY UA ON U.ID=UA.USER_ID LEFT JOIN USER_SKILL US ON U.ID=US.USER_ID,ROLE R,USER_PREFERRED_ZIP_CODE UPZC " +
                "WHERE U.ROLE_ID=R.ID AND U.ID=UPZC.USER_ID AND R.NAME=:role AND (U.NAME LIKE :search OR U.EMAIL LIKE :search OR U.TELEPHONE LIKE :search OR " +
                "U.ADDRESS_LINE_1 LIKE :search OR U.ADDRESS_LINE_2 LIKE :search OR U.ZIP_CODE LIKE :search)";

        if (!abilities.isEmpty()) {
            sql += " OR UA.ABILITY_ID = :abilities ";
        }

        if (!skills.isEmpty()) {
            if (skills.split(",").length > 1) {
                String newSkills = "";

                for (int i = 0; i < skills.split(",").length; i++) {
                    if (i == skills.split(",").length - 1) {
                        newSkills += "'" + skills.split(",")[i] + "'";
                    } else {
                        newSkills += "'" + skills.split(",")[i] + "',";
                    }
                }

                sql += " OR US.SKILL_ID IN (" + newSkills + ")";
            } else {
                sql += " OR US.SKILL_ID = '" + skills + "'";
            }
        }

        sql += " HAVING DISTANCE <= :distance) x";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("search", "%" + search + "%");
        mapSqlParameterSource.addValue("longitude", longitude);
        mapSqlParameterSource.addValue("latitude", latitude);
        mapSqlParameterSource.addValue("distance", distance);
        mapSqlParameterSource.addValue("role", role);

        if (!abilities.isEmpty()) {
            mapSqlParameterSource.addValue("abilities", abilities);
        }

        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, (rs, rowNum) -> rs.getInt("TOTAL"));
    }

    @Override
    public int update(User user) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("telephone", user.getTelephone())
                .addValue("addressLine1", user.getAddressLine1())
                .addValue("addressLine2", user.getAddressLine2())
                .addValue("zipCode", user.getZipCode())
                .addValue("longitude", user.getLongitude())
                .addValue("latitude", user.getLatitude())
                .addValue("id", user.getId());

        if (user.getLinkedInUrl() != null) {
            params.addValue("linkedinUrl", user.getLinkedInUrl());
        } else {
            params.addValue("linkedinUrl", "");
        }


        if (user.getTwitterUrl() != null) {
            params.addValue("twitterUrl", user.getTwitterUrl());
        } else {
            params.addValue("twitterUrl", "");
        }


        if (user.getFacebookUrl() != null) {
            params.addValue("facebookUrl", user.getFacebookUrl());
        } else {
            params.addValue("facebookUrl", "");
        }

        if (user.getBio() != null) {
            params.addValue("bio", user.getBio());
        } else {
            params.addValue("bio", "");
        }

        if (user.getSmallInfo() != null) {
            params.addValue("smallInfo", user.getSmallInfo());
        } else {
            params.addValue("smallInfo", "");
        }

        if (user.getProfilePicture() != null) {
            params.addValue("profilePicture", user.getProfilePicture());
        } else {
            params.addValue("profilePicture", "");
        }

        String sql = "UPDATE USER SET NAME=:name, TELEPHONE=:telephone, ADDRESS_LINE_1=:addressLine1, " +
                "ADDRESS_LINE_2=:addressLine2, ZIP_CODE=:zipCode, LONGITUDE=:longitude, LATITUDE=:latitude, " +
                "SMALL_INFO = CASE WHEN :smallInfo IS NOT NULL THEN :smallInfo ELSE SMALL_INFO END, " +
                "BIO=:bio, LINKEDIN_URL = CASE WHEN :linkedinUrl IS NOT NULL THEN :linkedinUrl ELSE LINKEDIN_URL END, " +
                "TWITTER_URL = CASE WHEN :twitterUrl IS NOT NULL THEN :twitterUrl ELSE TWITTER_URL END, " +
                "FACEBOOK_URL = CASE WHEN :facebookUrl IS NOT NULL THEN :facebookUrl ELSE FACEBOOK_URL END, " +
                "PROFILE_PICTURE = CASE WHEN :profilePicture IS NOT NULL THEN :profilePicture ELSE PROFILE_PICTURE END " +
                "WHERE ID=:id";

        return namedParameterJdbcTemplate.update(sql, params);

//        String sql = "UPDATE USER SET NAME=:name,PROFILE_PICTURE=:profilePicture,TELEPHONE=:telephone,ADDRESS_LINE_1=:addressLine1," +
//                "ADDRESS_LINE_2=:addressLine2,ZIP_CODE=:zipCode,LONGITUDE=:longitude,LATITUDE=:latitude,SMALL_INFO=:smallInfo,BIO=:bio," +
//                "FACEBOOK_URL=:facebookUrl,TWITTER_URL=:twitterUrl,LINKEDIN_URL=:linkedinUrl WHERE ID=:id";
//        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(user));
    }

    @Override
    public int updatePassword(String id, String password) {
        String sql = "UPDATE USER SET PASSWORD=:password WHERE ID=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("password", password);
        return namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
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
        user.setLongitude(rs.getString("LONGITUDE"));
        user.setLatitude(rs.getString("LATITUDE"));
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
