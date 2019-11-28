package m3u8downloader;

import android.text.TextUtils;
import com.m3u8downloader.utils.AES128Utils;
import com.m3u8downloader.utils.MUtils;
import java.io.File;

public class M3U8EncryptHelper {
    public static void encryptFile(String str, String str2) throws Exception {
        if (!TextUtils.isEmpty(str)) {
            MUtils.saveFile(AES128Utils.getAESEncode(str, MUtils.readFile(str2)), str2);
        }
    }

    public static void decryptFile(String str, String str2) throws Exception {
        if (!TextUtils.isEmpty(str)) {
            MUtils.saveFile(AES128Utils.getAESDecode(str, MUtils.readFile(str2)), str2);
        }
    }

    public static String encryptFileName(String str, String str2) throws Exception {
        if (TextUtils.isEmpty(str)) {
            return str2;
        }
        return AES128Utils.parseByte2HexStr(AES128Utils.getAESEncode(str, str2));
    }

    public static String decryptFileName(String str, String str2) throws Exception {
        if (TextUtils.isEmpty(str)) {
            return str2;
        }
        return new String(AES128Utils.getAESDecode(str, AES128Utils.parseHexStr2Byte(str2)));
    }

    public static void encryptTsFilesName(String str, String str2) throws Exception {
        if (!TextUtils.isEmpty(str)) {
            File file = new File(str2);
            if (file.exists() && file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    if (!listFiles[i].getName().contains("m3u8")) {
                        listFiles[i].renameTo(new File(str2, encryptFileName(str, listFiles[i].getName())));
                    }
                }
            }
        }
    }

    public static void decryptTsFilesName(String str, String str2) throws Exception {
        if (!TextUtils.isEmpty(str)) {
            File file = new File(str2);
            if (file.exists() && file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    if (!listFiles[i].getName().contains("m3u8")) {
                        listFiles[i].renameTo(new File(str2, decryptFileName(str, listFiles[i].getName())));
                    }
                }
            }
        }
    }
}
