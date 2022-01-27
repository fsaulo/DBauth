import com.felix.simpledb.utils.Encryption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptionTest {

    @Test
    void givenStringWhenEncryptThenSuccess()
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        String input = "Testing a long string with AES/CBC/PKCSPadding encryption";
        SecretKey key = Encryption.generateKey(128);
        IvParameterSpec ivParameterSpec = Encryption.generateIv();
        String algorithm = "AES/CBC/PKCS5Padding";
        String cipherText = Encryption.encrypt(algorithm, input, key, ivParameterSpec);
        String plainText = Encryption.decrypt(algorithm, cipherText, key, ivParameterSpec);
        Assertions.assertEquals(input, plainText);
    }

    @Test
    void givenStringWhenEncryptWithoutIVThenSuccess()
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException {

        String input = "Testing a long string with AES/ECB/PKCSPadding encryption without IV";
        SecretKey key = Encryption.generateKey(128);
        String algorithm = "AES/ECB/PKCS5Padding";
        String cipherText = Encryption.encrypt(algorithm, input, key);
        String plainText = Encryption.decrypt(algorithm, cipherText, key);
        Assertions.assertEquals(input, plainText);
    }

    @Test
    void givenSecretKeyTryToEncodeThenDecode()
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        String input = "Top secret text";
        SecretKey key = (Encryption.generateKey(256));
        String encodedKey = Encryption.encodeSecretKey(key);
        SecretKey key1 = Encryption.decodeSecretKey(encodedKey);

        System.out.println(encodedKey);

        String algorithm = "AES/ECB/PKCS5Padding";
        String cipherText = Encryption.encrypt(algorithm, input, key);
        String plainText = Encryption.decrypt(algorithm, cipherText, key1);

        Assertions.assertEquals(plainText, input);
    }
}
