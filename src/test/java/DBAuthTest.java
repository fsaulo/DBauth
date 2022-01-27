import com.felix.simpledb.utils.DBAuth;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DBAuthTest {

    final String key1 = "dqAS123SDA88DddA97DQ77X@DGGQH";
    final String key2 = "dqAS123SDA88DddA97DQ77#1asdAH";
    final String key3 = "1D233@#!23341244S301772ADGGQH";
    final String key4 = "xaSS123SDA88DddA97DQ77X@DQQDK";
    final String key5 = "KJASJHDHhhjasjj@J37&*12(#((13";
    final String key6 = "ASUIY77jHkjh87#$!08jkjas97HjH";

    @Test
    public void generateSomeKeysThenSaveToFile() throws IOException {
        DBAuth.saveKey(key1);
        DBAuth.saveKey(key2);
        DBAuth.saveKey(key3);
        DBAuth.saveKey(key4);
        DBAuth.saveKey(key5);
        Assertions.assertTrue(new File(DBAuth.databaseHashesFileName).exists());
    }

    @Test
    public void checkAllEntriesSavedInFile() throws IOException, ParseException {
        ArrayList<Object> array = DBAuth.readKeys();
        Assertions.assertNotNull(array);
    }

    @Test
    public void generateKeyMakeSureItSavedToFile() throws IOException, ParseException {
        String key = key1;
        DBAuth.saveKey(key);
        ArrayList<Object> array = DBAuth.readKeys();
        Assertions.assertTrue(array.contains(key));
    }

    @Test
    public void addAndTryToRemoveKeyFromFile() throws IOException, ParseException {
        String key = key5 ;
        DBAuth.saveKey(key);
        ArrayList<Object> array = DBAuth.readKeys();
        Assertions.assertTrue(array.contains(key));
        DBAuth.removeKey(key);
        array = DBAuth.readKeys();
        Assertions.assertFalse(array.contains(key));
    }

    @Test
    public void setDatabaseSaltMakeSureEquals() throws IOException, ParseException {
        String salt = key6;
        DBAuth.setDatabaseSalt(salt);
        String key = DBAuth.getDatabaseSalt();
        Assertions.assertEquals(key, salt);
    }

    @Test
    public void checkAllEncodedEntries() throws IOException, ParseException {
        ArrayList<Object> array = DBAuth.readKeys();
        for (Object entry : array) {
            System.out.println(entry);
        }
    }

    @Test
    public void tryToRemoveAllAddedKeysFromFile() throws IOException, ParseException {
        DBAuth.removeKey(key1);
        DBAuth.removeKey(key2);
        DBAuth.removeKey(key3);
        DBAuth.removeKey(key4);
        DBAuth.removeKey(key5);
        ArrayList<Object> array = DBAuth.readKeys();
        Assertions.assertFalse(array.contains(key1));
        Assertions.assertFalse(array.contains(key2));
        Assertions.assertFalse(array.contains(key3));
        Assertions.assertFalse(array.contains(key4));
        Assertions.assertFalse(array.contains(key5));
    }
}
