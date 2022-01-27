package com.felix.simpledb.model;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public interface DataAccessObject {
    void setup() throws SQLException, NoSuchAlgorithmException, IOException, ParseException;
    void connect() throws Exception;
    void close() throws Exception;
}
