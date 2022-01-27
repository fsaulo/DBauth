package com.felix.simpledb.model;

import com.felix.simpledb.utils.DBAuth;
import com.felix.simpledb.utils.Encryption;
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
import java.util.List;

public class Management {
    private final UserDao userDao;
    private final String algorithm = Encryption.AES_ECB_PKCS5_PADDING;

    public Management(UserDao user) {
        this.userDao = user;
        open();
    }

    public void addUser(String name, String username, String password)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, SQLException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException, IOException, ParseException {

        int length = 256;
        String salt = Encryption.encodeSecretKey(Encryption.generateKey(length));
        String dbSalt = DBAuth.getDatabaseSalt();

        String dbKey = DBAuth.getDatabaseKey();

        SecretKey dBUserKeySalted = Encryption.generateKey(password, dbSalt);
        SecretKey userKeySalted = Encryption.generateKey(password, salt);

        String encryptedPassword = Encryption.encrypt(algorithm, password, userKeySalted);
        String encryptedDBKey = Encryption.encrypt(algorithm, dbKey, dBUserKeySalted);

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setSalt(salt);

        userDao.insertUser(user);
        DBAuth.saveKey(encryptedDBKey);
    }

    public boolean authenticate (String username, String password)
            throws SQLException, NoSuchAlgorithmException, IOException, ParseException {
        User user = userDao.fetchUserDataByUsername(username);
        if (user != null) {
            String encryptedPassword = user.getPassword();
            System.out.println("Everything is fine for now...");
            System.out.println("Encrypted password: " + encryptedPassword);
            try {
                SecretKey key = Encryption.generateKey(password, user.getSalt());
                String decryptedPassword = Encryption.decrypt(algorithm, encryptedPassword, key);
                System.out.println("Password matches");
                System.out.println("Decrypted password: " + decryptedPassword);
                System.out.println("Access granted");
                System.out.println("------------------------------------------------");
                return true;
            } catch (BadPaddingException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException |
                    IllegalBlockSizeException | NoSuchAlgorithmException e) {
                System.out.println("Invalid key");
                return false;
            }
        }
        return false;
    }

    public List<User> listUsers() {
        return userDao.findAll();
    }

    private void open() {
        try {
            this.userDao.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            userDao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
