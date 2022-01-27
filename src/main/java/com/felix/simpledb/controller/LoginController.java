package com.felix.simpledb.controller;

import com.felix.simpledb.model.Management;
import com.felix.simpledb.model.User;
import com.felix.simpledb.model.UserDao;
import com.felix.simpledb.model.UserService;
import com.felix.simpledb.utils.DBAuth;
import com.felix.simpledb.utils.Encryption;
import com.felix.simpledb.view.View;
import com.felix.simpledb.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.json.simple.parser.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginController {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    protected void onLogin() throws Exception {
        if (checkLogin()) {
            ViewSwitcher.switchTo(View.MAIN);
        }
    }

    public boolean checkLogin()
            throws NoSuchAlgorithmException, SQLException, IOException, ParseException,
            InvalidKeySpecException {

        User user = new User();
        user.setUsername(username.getText());
        user.setPassword(password.getText());

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
            } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException |
                    InvalidKeyException ex) {
                System.out.println("Failed.");
            }
        }

        if (plain.isEmpty()) {
            System.out.println("Access denied.");
            System.out.println("------------------------------------------------");
            return false;
        }

        UserDao userDao = new UserService();
        DBAuth.setDatabaseKey(plain);
        userDao.setup();
        Management model = new Management(userDao);
        return model.authenticate(user.getUsername(), user.getPassword());
    }
}
