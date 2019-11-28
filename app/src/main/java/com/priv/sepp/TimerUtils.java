package com.priv.sepp;

public class TimerUtils {
    public static String getTime(int i) {
        StringBuilder stringBuilder;
        int i2;
        if (i < 10) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("00:0");
            stringBuilder.append(i);
            return stringBuilder.toString();
        } else if (i < 60) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("00:");
            stringBuilder.append(i);
            return stringBuilder.toString();
        } else if (i < 3600) {
            i2 = i / 60;
            i -= i2 * 60;
            if (i2 < 10) {
                if (i < 10) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("0");
                    stringBuilder.append(i2);
                    stringBuilder.append(":0");
                    stringBuilder.append(i);
                    return stringBuilder.toString();
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("0");
                stringBuilder.append(i2);
                stringBuilder.append(":");
                stringBuilder.append(i);
                return stringBuilder.toString();
            } else if (i < 10) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(i2);
                stringBuilder.append(":0");
                stringBuilder.append(i);
                return stringBuilder.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(i2);
                stringBuilder.append(":");
                stringBuilder.append(i);
                return stringBuilder.toString();
            }
        } else {
            i2 = i / 3600;
            i -= i2 * 3600;
            int i3 = i / 60;
            i -= i3 * 60;
            if (i2 < 10) {
                if (i3 < 10) {
                    if (i < 10) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("0");
                        stringBuilder.append(i2);
                        stringBuilder.append(":0");
                        stringBuilder.append(i3);
                        stringBuilder.append(":0");
                        stringBuilder.append(i);
                        return stringBuilder.toString();
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("0");
                    stringBuilder.append(i2);
                    stringBuilder.append(":0");
                    stringBuilder.append(i3);
                    stringBuilder.append(":");
                    stringBuilder.append(i);
                    return stringBuilder.toString();
                } else if (i < 10) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("0");
                    stringBuilder.append(i2);
                    stringBuilder.append(i3);
                    stringBuilder.append(":0");
                    stringBuilder.append(i);
                    return stringBuilder.toString();
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("0");
                    stringBuilder.append(i2);
                    stringBuilder.append(i3);
                    stringBuilder.append(":");
                    stringBuilder.append(i);
                    return stringBuilder.toString();
                }
            } else if (i3 < 10) {
                if (i < 10) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(i2);
                    stringBuilder.append(":0");
                    stringBuilder.append(i3);
                    stringBuilder.append(":0");
                    stringBuilder.append(i);
                    return stringBuilder.toString();
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(i2);
                stringBuilder.append(":0");
                stringBuilder.append(i3);
                stringBuilder.append(":");
                stringBuilder.append(i);
                return stringBuilder.toString();
            } else if (i < 10) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(i2 + i3);
                stringBuilder.append(":0");
                stringBuilder.append(i);
                return stringBuilder.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(i2 + i3);
                stringBuilder.append(":");
                stringBuilder.append(i);
                return stringBuilder.toString();
            }
        }
    }
}
