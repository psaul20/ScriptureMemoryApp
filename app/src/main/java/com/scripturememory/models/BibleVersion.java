package com.scripturememory.models;

/**
 * Created by Tyler on 12/28/17.
 */

public class BibleVersion implements Comparable<BibleVersion> {

    private String versionCode;
    private String versionName;
    private String lngCode;

    public BibleVersion () {}

    public BibleVersion (String versionCode, String versionName, String lngCode) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.lngCode = lngCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getLngCode() {
        return lngCode;
    }

    public void setLngCode(String lngCode) {
        this.lngCode = lngCode;
    }

    @Override
    public int compareTo(BibleVersion bibleVersion) {
        return versionName.compareTo(bibleVersion.getVersionName());
    }
}
