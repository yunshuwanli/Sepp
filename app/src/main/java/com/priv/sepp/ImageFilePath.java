package com.priv.sepp;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;

public class ImageFilePath {
    public static String getPath(Context context, Uri uri) {
        Uri uri2 = null;
        if ((VERSION.SDK_INT >= 19 ? 1 : null) == null || !DocumentsContract.isDocumentUri(context, uri)) {
            if (Param.CONTENT.equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } else if (isExternalStorageDocument(uri)) {
            String[] split = DocumentsContract.getDocumentId(uri).split(":");
            if ("primary".equalsIgnoreCase(split[0])) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Environment.getExternalStorageDirectory());
                stringBuilder.append("/");
                stringBuilder.append(split[1]);
                return stringBuilder.toString();
            }
        } else if (isDownloadsDocument(uri)) {
            return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
        } else if (isMediaDocument(uri)) {
            Object obj = DocumentsContract.getDocumentId(uri).split(":")[0];
            if (TtmlNode.TAG_IMAGE.equals(obj)) {
                uri2 = Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(obj)) {
                uri2 = Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(obj)) {
                uri2 = Audio.Media.EXTERNAL_CONTENT_URI;
            }
            return getDataColumn(context, uri2, "_id=?", new String[]{r6[1]});
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0037  */
    public static String getDataColumn(Context r7, Uri r8, String r9, String[] r10) {
        /*
        r0 = "_data";
        r3 = new java.lang.String[]{r0};
        r0 = 0;
        r1 = r7.getContentResolver();	 Catch:{ all -> 0x0033 }
        r6 = 0;
        r2 = r8;
        r4 = r9;
        r5 = r10;
        r7 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ all -> 0x0033 }
        if (r7 == 0) goto L_0x002d;
    L_0x0015:
        r8 = r7.moveToFirst();	 Catch:{ all -> 0x002b }
        if (r8 == 0) goto L_0x002d;
    L_0x001b:
        r8 = "_data";
        r8 = r7.getColumnIndexOrThrow(r8);	 Catch:{ all -> 0x002b }
        r8 = r7.getString(r8);	 Catch:{ all -> 0x002b }
        if (r7 == 0) goto L_0x002a;
    L_0x0027:
        r7.close();
    L_0x002a:
        return r8;
    L_0x002b:
        r8 = move-exception;
        goto L_0x0035;
    L_0x002d:
        if (r7 == 0) goto L_0x0032;
    L_0x002f:
        r7.close();
    L_0x0032:
        return r0;
    L_0x0033:
        r8 = move-exception;
        r7 = r0;
    L_0x0035:
        if (r7 == 0) goto L_0x003a;
    L_0x0037:
        r7.close();
    L_0x003a:
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.ImageFilePath.getDataColumn(android.content.Context, android.net.Uri, java.lang.String, java.lang.String[]):java.lang.String");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
