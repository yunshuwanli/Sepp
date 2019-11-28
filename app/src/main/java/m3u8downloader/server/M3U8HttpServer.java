package m3u8downloader.server;

import android.net.Uri;

import com.google.android.exoplayer2.util.MimeTypes;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class M3U8HttpServer extends NanoHTTPD {
    private static final int DEFAULT_PORT = 8686;
    public String filesDir = null;
    private FileInputStream fis;
    private NanoHTTPD server;

    public M3U8HttpServer() {
        super(DEFAULT_PORT);
    }

    public M3U8HttpServer(int i) {
        super(i);
    }

    public String createLocalHttpUrl(String str) {
        Uri parse = Uri.parse(str);
        if (parse.getScheme() != null) {
            str = parse.toString();
        } else {
            str = parse.getPath();
        }
        if (str == null) {
            return null;
        }
        this.filesDir = str.substring(0, str.lastIndexOf("/") + 1);
        return String.format("http://127.0.0.1:%d%s", new Object[]{Integer.valueOf(DEFAULT_PORT), str});
    }

    public void execute() {
        try {
            this.server = (NanoHTTPD) M3U8HttpServer.class.newInstance();
            this.server.start(5000, true);
        } catch (IOException unused) {
            System.exit(-1);
        } catch (Exception unused2) {
            System.exit(-1);
        }
    }

    public void finish() {
        NanoHTTPD nanoHTTPD = this.server;
        if (nanoHTTPD != null) {
            nanoHTTPD.stop();
            this.server = null;
        }
    }

    public Response serve(IHTTPSession iHTTPSession) {
        String valueOf = String.valueOf(iHTTPSession.getUri());
        File file = new File(valueOf);
        Response.IStatus iStatus = Response.Status.NOT_FOUND;
        String str = NanoHTTPD.MIME_HTML;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("文件不存在：");
        stringBuilder.append(valueOf);
        Response newFixedLengthResponse = NanoHTTPD.newFixedLengthResponse(iStatus, str, stringBuilder.toString());
        if (file.exists()) {
            try {
                this.fis = new FileInputStream(file);
                String str2 = MimeTypes.VIDEO_MPEG;
                if (valueOf.contains(".m3u8")) {
                    str2 = "video/x-mpegURL";
                }
                try {
                    newFixedLengthResponse = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, str2, this.fis, (long) this.fis.available());
                } catch (IOException unused) {
                    return newFixedLengthResponse;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
        }
        Response.IStatus iStatus2 = Response.Status.NOT_FOUND;
        String str3 = NanoHTTPD.MIME_HTML;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("文件不存在：");
        stringBuilder2.append(valueOf);
        return NanoHTTPD.newFixedLengthResponse(iStatus2, str3, stringBuilder2.toString());
    }
}
