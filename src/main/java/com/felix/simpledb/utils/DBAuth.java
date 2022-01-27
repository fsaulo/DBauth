package com.felix.simpledb.utils;

import com.felix.simpledb.model.User;
import com.felix.simpledb.model.UserService;
import com.felix.simpledb.utils.Encryption;
import com.google.gson.JsonParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

public class DBAuth {
    public static final String databaseHashesFileName = "simpledb_auth.json";
    private static String databaseKey;
    private static String databaseSalt;

    public static FileReader initFile (String fileName) throws IOException {
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hashes", jsonArray);
            jsonObject.put("salt", new JSONObject());
            Files.write(Paths.get(fileName), jsonObject.toJSONString().getBytes());
        }

        return new FileReader(fileName);
    }

    public static void setup() throws NoSuchAlgorithmException, IOException,
           ParseException {
        String key = Encryption.generateSecureRandomPassword();
        String salt = Encryption.encodeSecretKey(Encryption.generateKey(256));
        DBAuth.setDatabaseKey(key);
        DBAuth.setDatabaseSalt(salt);
    }

    public static String getDatabaseSalt() throws IOException, ParseException {
        FileReader reader = initFile(databaseHashesFileName);
        JSONParser parser = new JSONParser();
        JSONObject object;
        object = (JSONObject) parser.parse(reader);
        return (String) object.get("salt");
    }

    public static void setDatabaseSalt(String databaseSalt) throws IOException, ParseException {
        FileReader reader = initFile(databaseHashesFileName);
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(reader);
        object.put("salt", databaseSalt);
        Files.write(Paths.get(databaseHashesFileName), object.toJSONString().getBytes());
        DBAuth.databaseSalt = (String) object.get("salt");
    }

    public static String getDatabaseKey() {
        return databaseKey;
    }

    public static void setDatabaseKey(String databaseKey) {
        DBAuth.databaseKey = databaseKey;
    }

    public static void saveKey(String key) throws IOException {

        JSONParser parser = new JSONParser();
        JSONArray hashes = new JSONArray();
        JSONObject object = new JSONObject();
        FileReader reader = initFile(databaseHashesFileName);

        try {
            object = (JSONObject) parser.parse(reader);
            hashes = (JSONArray) object.get("hashes");
            if (!hashes.contains(key)) hashes.add(key);
        } catch (JsonParseException | ParseException e1) {
            initFile(databaseHashesFileName);
        } catch (NullPointerException e2) {
            hashes = new JSONArray();
            hashes.add(key);
        }

        object.put("hashes", hashes);
        Files.write(Paths.get(databaseHashesFileName), object.toJSONString().getBytes());
    }

    public static ArrayList<Object> readKeys() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray hashes;
        ArrayList<Object> result = null;
        FileReader reader;
        try {
            reader = initFile(databaseHashesFileName);
            hashes = (JSONArray) ((JSONObject) parser.parse(reader)).get("hashes");
            result = new ArrayList<Object>(hashes);
        } catch (NullPointerException e1) {
            return new ArrayList<>();
        } catch (FileNotFoundException e2) {
            initFile(databaseHashesFileName);
        }
        return result;
    }

    public static void removeKey(String key) throws IOException {

        JSONParser parser = new JSONParser();
        JSONArray hashes = new JSONArray();
        JSONObject object = new JSONObject();
        FileReader reader = initFile(databaseHashesFileName);

        try {
            object = (JSONObject) parser.parse(reader);
            hashes = (JSONArray) object.get("hashes");
            hashes.remove(key);
        } catch (JsonParseException | ParseException e1) {
            initFile(databaseHashesFileName);
        } catch (NullPointerException e2) {
            hashes = new JSONArray();
        }

        object.put("hashes", hashes);
        Files.write(Paths.get(databaseHashesFileName), object.toJSONString().getBytes());
    }

}
