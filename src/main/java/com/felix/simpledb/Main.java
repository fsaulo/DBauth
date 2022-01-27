package com.felix.simpledb;

import com.felix.simpledb.model.Management;
import com.felix.simpledb.model.User;
import com.felix.simpledb.model.UserDao;
import com.felix.simpledb.model.UserService;
import com.felix.simpledb.utils.Encryption;
import com.felix.simpledb.utils.DBAuth;
import com.felix.simpledb.view.View;
import com.felix.simpledb.view.ViewSwitcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.sql.SQLException;
import java.util.ArrayList;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        var scene = new Scene(new StackPane(), 320, 240);

        ViewSwitcher.setScene(scene);
        ViewSwitcher.switchTo(View.LOGIN);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @SuppressWarnings("unused")
    public static void testDB() throws Exception {
        UserDao userDao = new UserService();
        userDao.setup();
        Management model = new Management(userDao);

        try {
            model.addUser("Saulo", "fsaulo", "123");
            model.addUser("Isaac", "isaac", "1s44c");
            model.addUser("Ju", "ju", "ju13");
            model.addUser("Rute", "carol", "rut3");
        } catch (SQLException e) {
            System.out.println("User already exists.");
        }


        for (User user : model.listUsers()) {
            System.out.println(user.getPassword());
        }

    }

    @SuppressWarnings("unused")
    public static void testListUsers() throws Exception {
        UserDao userDao = new UserService();
        userDao.setup();
        Management model = new Management(userDao);
        System.out.println("Listing all users...");
        for (User user : model.listUsers()) {
            System.out.println(user.getName());
        }
    }

    @SuppressWarnings("unused")
    public static void testAutoDecryption() throws Exception {

        User user = new User();
        user.setUsername("carol");
        user.setPassword("ju123");

        String algorithm = Encryption.AES_ECB_PKCS5_PADDING;
        String dbSalt = DBAuth.getDatabaseSalt();
        String plain = "";
        ArrayList<Object> hashes = DBAuth.readKeys();
        SecretKey key = Encryption.generateKey(user.getPassword(), dbSalt);
        System.out.println("KEY: " + Encryption.encodeSecretKey(key));

        for (Object hash : hashes) {
            System.out.println("HASHED_KEY: " + hash);
            System.out.println("Trying to decrypt...");
            try {
                plain = Encryption.decrypt(algorithm, (String) hash, key);
                System.out.println("Decrypted.");
                System.out.println("DATABASE_DECRYPTED_KEY: " + plain);
                System.out.println("------------------------------------------------");
                break;
            } catch (Exception ex) {
                System.out.println("Failed.");
            }
        }

        if (plain.isEmpty()) {
            System.out.println("Access denied.");
            return;
        }

        UserDao userDao = new UserService();
        DBAuth.setDatabaseKey(plain);
        userDao.setup();
        Management model = new Management(userDao);
        model.authenticate(user.getUsername(), user.getPassword());
    }

    @SuppressWarnings("unused")
    public static void testManualDecryption() throws Exception {
        String algorithm = "AES/ECB/PKCS5Padding";
        String dbEnc = "IWiAAAOY4YWJSUwKNkTUewK2QcdP6NxbwsVnbmpwPTg=";
        String dbSalt = DBAuth.getDatabaseSalt();
        SecretKey key = Encryption.generateKey("rut3", dbSalt);
        String plain = Encryption.decrypt(algorithm, dbEnc, key);
        System.out.println(plain);
    }
}
