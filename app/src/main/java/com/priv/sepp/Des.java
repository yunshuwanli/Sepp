package com.priv.sepp;

import android.util.Base64;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Des {
    public static final String ALGORITHM = "DES";
    private static final Charset charset = Charset.forName("UTF-8");
    private static final String key0 = "D";
    private static byte[] keyBytes = key0.getBytes(charset);

    public static byte[] decryptBASE64(String str) throws Exception {
        return Base64.decode(str, 0);
    }

    public static String encryptBASE64(byte[] bArr) throws Exception {
        return Base64.encodeToString(bArr, 0);
    }

    private static Key toKey(byte[] bArr) throws Exception {
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(new DESKeySpec(bArr));
    }

    public static byte[] decrypt(byte[] bArr, String str) throws Exception {
        Key toKey = toKey(decryptBASE64(str));
        Cipher instance = Cipher.getInstance(ALGORITHM);
        instance.init(2, toKey);
        return instance.doFinal(bArr);
    }

    public static byte[] encrypt(byte[] bArr, String str) throws Exception {
        Key toKey = toKey(decryptBASE64(str));
        Cipher instance = Cipher.getInstance(ALGORITHM);
        instance.init(1, toKey);
        return instance.doFinal(bArr);
    }

    public static String initKey() throws Exception {
        return initKey(null);
    }

    public static String initKey(String str) throws Exception {
        SecureRandom secureRandom;
        if (str != null) {
            secureRandom = new SecureRandom(decryptBASE64(str));
        } else {
            secureRandom = new SecureRandom();
        }
        KeyGenerator instance = KeyGenerator.getInstance(ALGORITHM);
        instance.init(secureRandom);
        return encryptBASE64(instance.generateKey().getEncoded());
    }

    public static String encode(String str) {
        byte[] bytes = str.getBytes(charset);
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            for (byte b : keyBytes) {
                bytes[i] = (byte) (b ^ bytes[i]);
            }
        }
        return new String(bytes);
    }

    public static String decode(String str) {
        byte[] bytes = str.getBytes(charset);
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            for (byte b : keyBytes) {
                bytes[i] = (byte) (b ^ bytes[i]);
            }
        }
        return new String(bytes);
    }
}
