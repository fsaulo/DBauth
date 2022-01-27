package com.felix.simpledb.model;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public interface UserDao extends DataAccessObject {
    void insertUser(User user) throws SQLException, NoSuchAlgorithmException, IOException, ParseException;
    User fetchUserDataByUsername(String username) throws SQLException, NoSuchAlgorithmException, IOException, ParseException;

    List<User> findAll();
}
