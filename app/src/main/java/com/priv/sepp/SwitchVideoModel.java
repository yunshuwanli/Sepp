package com.priv.sepp;

public class SwitchVideoModel {
    private String name;
    private String url;

    public SwitchVideoModel(String str, String str2) {
        this.name = str;
        this.url = str2;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String toString() {
        return this.name;
    }
}
