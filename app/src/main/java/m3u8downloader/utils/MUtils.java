package m3u8downloader.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m3u8downloader.M3U8DownloaderConfig;
import m3u8downloader.bean.M3U8;
import m3u8downloader.bean.M3U8Ts;

public class MUtils {
    private static float KB = 1024.0f;
    private static float MB = (KB * 1024.0f);
    private static float GB = (MB * 1024.0f);

    public static M3U8 parseIndex(String str) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(str).openStream()));
        String substring = str.substring(0, str.lastIndexOf("/") + 1);
        M3U8 m3u8 = new M3U8();
        m3u8.setBasePath(substring);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getUrlPrefix(str));
        stringBuilder.append(getHost(str));
        m3u8.setDomain(stringBuilder.toString());
        float f = 0.0f;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                bufferedReader.close();
                return m3u8;
            } else if (readLine.startsWith("#")) {
                if (readLine.startsWith("#EXTINF:")) {
                    String substring2 = readLine.substring(8);
                    if (substring2.endsWith(",")) {
                        substring2 = substring2.substring(0, substring2.length() - 1);
                    }
                    f = Float.parseFloat(substring2);
                }
            } else if (readLine.endsWith("m3u8")) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(substring);
                stringBuilder2.append(readLine);
                return parseIndex(stringBuilder2.toString());
            } else {
                m3u8.addTs(new M3U8Ts(readLine, f));
                f = 0.0f;
            }
        }
    }

    public static boolean clearDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (File clearDir : listFiles) {
                    clearDir(clearDir);
                }
                return file.delete();
            }
        }
        return true;
    }

    public static String formatFileSize(long j) {
        float f = (float) j;
        if (f >= GB) {
            return String.format("%.1f GB", new Object[]{Float.valueOf(f / GB)});
        }
        float f2 = MB;
        if (f >= f2) {
            return String.format(f / f2 > 100.0f ? "%.0f MB" : "%.1f MB", new Object[]{Float.valueOf(f / f2)});
        }
        f2 = KB;
        if (f >= f2) {
            return String.format(f / f2 > 100.0f ? "%.0f KB" : "%.1f KB", new Object[]{Float.valueOf(f / f2)});
        }
        return String.format("%d B", new Object[]{Long.valueOf(j)});
    }

    public static File createLocalM3U8(File file, String str, M3U8 m3u8) throws IOException {
        return createLocalM3U8(file, str, m3u8, null);
    }

    public static File createLocalM3U8(File file, String str, M3U8 m3u8, String str2) throws IOException {
        File file2 = new File(file, str);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file2, false));
        bufferedWriter.write("#EXTM3U\n");
        bufferedWriter.write("#EXT-X-VERSION:3\n");
        bufferedWriter.write("#EXT-X-MEDIA-SEQUENCE:0\n");
        bufferedWriter.write("#EXT-X-TARGETDURATION:13\n");
        for (M3U8Ts m3U8Ts : m3u8.getTsList()) {
            StringBuilder stringBuilder;
            if (str2 != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("#EXT-X-KEY:METHOD=AES-128,URI=\"");
                stringBuilder.append(str2);
                stringBuilder.append("\"\n");
                bufferedWriter.write(stringBuilder.toString());
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("#EXTINF:");
            stringBuilder.append(m3U8Ts.getSeconds());
            stringBuilder.append(",\n");
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.write(m3U8Ts.obtainEncodeTsFileName());
            bufferedWriter.newLine();
        }
        bufferedWriter.write("#EXT-X-ENDLIST");
        bufferedWriter.flush();
        bufferedWriter.close();
        return file2;
    }

    public static byte[] readFile(String str) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(str));
        byte[] bArr = new byte[fileInputStream.available()];
        fileInputStream.read(bArr);
        fileInputStream.close();
        return bArr;
    }

    public static void saveFile(byte[] bArr, String str) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
        fileOutputStream.write(bArr);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static String getSaveFileDir(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(M3U8DownloaderConfig.getSaveDir());
        stringBuilder.append(File.separator);
        stringBuilder.append(MD5Utils.encode(str));
        return stringBuilder.toString();
    }

    public static String getHost(String str) {
        if (str == null || str.trim().equals("")) {
            return "";
        }
        String str2 = "";
        Matcher matcher = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+").matcher(str);
        if (matcher.find()) {
            str2 = matcher.group();
        }
        return str2;
    }

    public static String getUrlPrefix(String str) {
        if (str.startsWith("http://")) {
            return "http://";
        }
        return str.startsWith("https://") ? "https://" : "";
    }
}
