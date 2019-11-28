package m3u8downloader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.Collections;
import java.util.Set;

public class SPHelper {
    private static final String NULL_KEY = "NULL_KEY";
    private static SharedPreferences PREFERENCES = null;
    private static final String TAG_NAME = "M3U8PreferenceHelper";

    public static void init(Context context) {
        PREFERENCES = context.getSharedPreferences(TAG_NAME, 0);
    }

    public static void onSetPrefBoolSetting(String str, Boolean bool, Context context) {
        if (str != null && bool != null && context != null) {
            context.getSharedPreferences(TAG_NAME, 0).edit().putBoolean(str, bool.booleanValue()).commit();
        }
    }

    private static String checkKeyNonNull(String str) {
        if (str != null) {
            return str;
        }
        Log.e(NULL_KEY, "Key is null!!!");
        return NULL_KEY;
    }

    private static Editor newEditor() {
        return PREFERENCES.edit();
    }

    public static void putBoolean(@NonNull String str, boolean z) {
        newEditor().putBoolean(checkKeyNonNull(str), z).apply();
    }

    public static boolean getBoolean(@NonNull String str, boolean z) {
        return PREFERENCES.getBoolean(checkKeyNonNull(str), z);
    }

    public static void putInt(@NonNull String str, int i) {
        newEditor().putInt(checkKeyNonNull(str), i).apply();
    }

    public static int getInt(@NonNull String str, int i) {
        return PREFERENCES.getInt(checkKeyNonNull(str), i);
    }

    public static void putLong(@NonNull String str, long j) {
        newEditor().putLong(checkKeyNonNull(str), j).apply();
    }

    public static long getLong(@NonNull String str, long j) {
        return PREFERENCES.getLong(checkKeyNonNull(str), j);
    }

    public static void putFloat(@NonNull String str, float f) {
        newEditor().putFloat(checkKeyNonNull(str), f).apply();
    }

    public static float getFloat(@NonNull String str, float f) {
        return PREFERENCES.getFloat(checkKeyNonNull(str), f);
    }

    public static void putString(@NonNull String str, @Nullable String str2) {
        newEditor().putString(checkKeyNonNull(str), str2).apply();
    }

    public static String getString(@NonNull String str, @Nullable String str2) {
        return PREFERENCES.getString(checkKeyNonNull(str), str2);
    }

    public static void putStringSet(@NonNull String str, @Nullable Set<String> set) {
        newEditor().putStringSet(checkKeyNonNull(str), set).apply();
    }

    public static Set<String> getStringSet(@NonNull String str, @Nullable Set<String> set) {
        Set stringSet = PREFERENCES.getStringSet(checkKeyNonNull(str), set);
        if (stringSet == null) {
            return null;
        }
        return Collections.unmodifiableSet(stringSet);
    }

    public static void increaseCount(String str) {
        putInt(str, getInt(str, 0) + 1);
    }

    public static void remove(String str) {
        newEditor().remove(str).apply();
    }

    public static void clearPreference() {
        newEditor().clear().commit();
    }
}
