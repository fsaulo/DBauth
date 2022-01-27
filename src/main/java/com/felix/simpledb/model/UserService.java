package com.felix.simpledb.model;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.felix.simpledb.utils.DBAuth;
import com.felix.simpledb.utils.Encryption;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.json.simple.parser.ParseException;
import org.sqlite.mc.SQLiteMCConfig;

public class UserService implements UserDao {
    private Connection connection;
    private final QueryRunner accessPoint = new QueryRunner();
    private static final List<User> EMPTY = new ArrayList<>();

    private Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    private boolean databaseExists() {
        return new File("simpledb.db").exists();
    }

    @Override
    public void setup() throws SQLException, NoSuchAlgorithmException, IOException, ParseException {

        String sql = "CREATE TABLE IF NOT EXISTS USER ("  +
                "ID       INTEGER UNIQUE NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME VARCHAR UNIQUE NOT NULL, " +
                "NAME     VARCHAR        NOT NULL, " +
                "PASSWORD VARCHAR        NOT NULL, " +
                "SALT     VARCHAR        NOT NULL)";

        connect();
        accessPoint.update(connection, sql);
    }

    @Override
    public void connect() throws SQLException, NoSuchAlgorithmException, IOException, ParseException {
        String key;
        String salt;

        if (databaseExists()) {
            key = DBAuth.getDatabaseKey();
        } else {
            DBAuth.setup();
            key = DBAuth.getDatabaseKey();
        }

        setConnection(DriverManager.getConnection("jdbc:sqlite:simpledb.db", new SQLiteMCConfig()
                        .withKey(key)
                        .toProperties()));
    }

    @Override
    public void close() throws SQLException {
        Connection conn = getConnection();
        if (conn != null)
            conn.close();
    }

    @Override
    public void insertUser(User user) throws SQLException, NoSuchAlgorithmException, 
           IOException, ParseException {
        Connection conn = getConnection();
        if (conn.isClosed())
            connect();

        try {
            String query = "INSERT INTO USER (NAME, USERNAME, PASSWORD, SALT) VALUES (?,?,?,?)";
            accessPoint.insert(conn, query, new ScalarHandler<BigDecimal>(), user.getName(),
                    user.getUsername(), user.getPassword(), user.getSalt());
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
        }
    }

    @Override
    public User fetchUserDataByUsername(String username) throws SQLException, NoSuchAlgorithmException,
            IOException, ParseException {

        Connection conn = getConnection();
        Object[] parameters;
        if (conn.isClosed()) {
            connect();
        }

        try {
            String query = "SELECT * FROM USER WHERE USERNAME = ?";
            parameters = new Object[1];
            parameters[0] = username;
            return accessPoint.query(conn, query, new BeanHandler<>(User.class), parameters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        Connection conn = getConnection();
        try {
            String query = "SELECT * FROM USER";
            return accessPoint.query(conn, query, new BeanListHandler<>(User.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY;
    }
}
