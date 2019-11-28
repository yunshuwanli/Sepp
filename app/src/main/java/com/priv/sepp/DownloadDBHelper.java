package com.priv.sepp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.m3u8downloader.bean.M3U8Task;
import java.io.PrintStream;
import java.util.ArrayList;

import m3u8downloader.bean.M3U8Task;

public class DownloadDBHelper extends SQLiteOpenHelper {
    public static final int INSERT_RESULT_DONE = 0;
    public static final int INSERT_RESULT_OK = 1;
    public static final int INSERT_RESULT_UNDONE = 2;
    private static final String TAG = "SQLiteInfo";
    public static final int VERSION = 1;

    public DownloadDBHelper(Context context, String str, CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.i(TAG, "create Database----------->");
        sQLiteDatabase.execSQL("create table DownloadMedia(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(128) , m3u8url VARCHAR(512),curTs INTEGER,totalTs INTEGER,secs INTEGER,icon VARCHAR(128),filesize VARCHAR(512))");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.i(TAG, "update Database------------->");
    }

    public int insertMedia(String str, String str2, int i, String str3, String str4, int i2) {
        M3U8Task queryMedia = queryMedia(str2);
        if (queryMedia == null) {
            SQLiteDatabase readableDatabase = getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", str);
            contentValues.put("m3u8url", str2);
            contentValues.put("curTs", Integer.valueOf(0));
            contentValues.put("secs", Integer.valueOf(i2));
            contentValues.put("totalTs", Integer.valueOf(i));
            contentValues.put("icon", str3);
            contentValues.put("filesize", "");
            readableDatabase.insert("DownloadMedia", null, contentValues);
            readableDatabase.close();
            return 1;
        } else if (queryMedia.getCurTs() != queryMedia.getTotalTs() || queryMedia.getTotalTs() <= 0) {
            Log.e(TAG, "下载任务已存在，单未完成，可以继续");
            return 2;
        } else {
            Log.e(TAG, "下载任务已存在，且已完成");
            return 0;
        }
    }

    public void deleteMedia(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String[] strArr = new String[]{String.valueOf(i)};
        readableDatabase.delete("DownloadMedia", "id=?", strArr);
        readableDatabase.close();
    }

    public void deleteMedia(String str) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String[] strArr = new String[]{str};
        readableDatabase.delete("DownloadMedia", "m3u8url=?", strArr);
        readableDatabase.close();
    }

    public void updateMedia(int i, int i2) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("curTs", Integer.valueOf(i2));
        String[] strArr = new String[]{String.valueOf(i)};
        readableDatabase.update("DownloadMedia", contentValues, "id=?", strArr);
        readableDatabase.close();
    }

    public void updateMedia(String str, int i, int i2) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("curTs", Integer.valueOf(i2));
        contentValues.put("totalTs", Integer.valueOf(i));
        String[] strArr = new String[]{str};
        readableDatabase.update("DownloadMedia", contentValues, "m3u8url=?", strArr);
        readableDatabase.close();
    }

    public void updateMediaSize(String str, String str2) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("filesize", str2);
        String[] strArr = new String[]{str};
        readableDatabase.update("DownloadMedia", contentValues, "m3u8url=?", strArr);
        readableDatabase.close();
    }

    public void deleteAll() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("DownloadMedia", null, null);
        readableDatabase.close();
    }

    public M3U8Task queryMedia(String str) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String str2 = "DownloadMedia";
        String[] strArr = new String[]{TtmlNode.ATTR_ID, "name", "m3u8url", "curTs", "totalTs", "icon", "filesize", "secs"};
        SQLiteDatabase sQLiteDatabase = readableDatabase;
        Cursor query = sQLiteDatabase.query(str2, strArr, "m3u8url=?", new String[]{str}, null, null, null);
        M3U8Task m3U8Task = null;
        if (query.getCount() <= 0) {
            return null;
        }
        while (query.moveToNext()) {
            str2 = query.getString(query.getColumnIndex("name"));
            String string = query.getString(query.getColumnIndex("m3u8url"));
            String string2 = query.getString(query.getColumnIndex("icon"));
            int i = query.getInt(query.getColumnIndex("curTs"));
            int i2 = query.getInt(query.getColumnIndex("totalTs"));
            int i3 = query.getInt(query.getColumnIndex("secs"));
            String string3 = query.getString(query.getColumnIndex("filesize"));
            M3U8Task m3U8Task2 = new M3U8Task(string);
            m3U8Task2.setName(str2);
            m3U8Task2.setCurTs(i);
            m3U8Task2.setTotalTs(i2);
            m3U8Task2.setIcon(string2);
            m3U8Task2.setFileSize(string3);
            m3U8Task2.setSecs(i3);
            PrintStream printStream = System.out;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("query----->影片名:");
            stringBuilder.append(str2);
            stringBuilder.append(",m3u8url:");
            stringBuilder.append(string);
            stringBuilder.append(",当前进度：");
            stringBuilder.append(i);
            stringBuilder.append(",总片数：");
            stringBuilder.append(i2);
            printStream.println(stringBuilder.toString());
            m3U8Task = m3U8Task2;
        }
        readableDatabase.close();
        return m3U8Task;
    }

    public ArrayList<M3U8Task> queryAllMedia() {
        ArrayList<M3U8Task> arrayList = new ArrayList();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor query = readableDatabase.query("DownloadMedia", new String[]{TtmlNode.ATTR_ID, "name", "m3u8url", "curTs", "totalTs", "icon", "filesize", "secs"}, null, null, null, null, null);
        while (query.moveToNext()) {
            String string = query.getString(query.getColumnIndex("name"));
            String string2 = query.getString(query.getColumnIndex("m3u8url"));
            String string3 = query.getString(query.getColumnIndex("icon"));
            String string4 = query.getString(query.getColumnIndex("filesize"));
            int i = query.getInt(query.getColumnIndex("curTs"));
            int i2 = query.getInt(query.getColumnIndex("totalTs"));
            int i3 = query.getInt(query.getColumnIndex("secs"));
            M3U8Task m3U8Task = new M3U8Task(string2);
            m3U8Task.setName(string);
            m3U8Task.setCurTs(i);
            m3U8Task.setTotalTs(i2);
            m3U8Task.setIcon(string3);
            m3U8Task.setFileSize(string4);
            m3U8Task.setSecs(i3);
            arrayList.add(m3U8Task);
            PrintStream printStream = System.out;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("query----->影片名:");
            stringBuilder.append(string);
            stringBuilder.append(",m3u8url:");
            stringBuilder.append(string2);
            stringBuilder.append(",当前进度：");
            stringBuilder.append(i);
            stringBuilder.append(",总片数：");
            stringBuilder.append(i2);
            stringBuilder.append(",secs=");
            stringBuilder.append(i3);
            printStream.println(stringBuilder.toString());
        }
        readableDatabase.close();
        return arrayList;
    }
}
