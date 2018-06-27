package com.scripturememory.models;

/**
 * Created by Tyler on 12/28/17.
 */

public class BibleVersion {

    private String versionCode;
    private String versionName;
    private String lngCode;

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
}
