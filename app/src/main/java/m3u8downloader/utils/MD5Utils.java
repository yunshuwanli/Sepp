package m3u8downloader.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Utils {
    public static String encode(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            str = new BigInteger(1, instance.digest()).toString(16);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }
}
