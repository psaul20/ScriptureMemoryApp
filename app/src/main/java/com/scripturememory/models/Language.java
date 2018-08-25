package com.scripturememory.models;

import android.support.annotation.NonNull;

/**
 * Created by Tyler on 12/28/17.
 */

public class Language implements Comparable<Language> {

    private String lngCode;
    private String lngName;

    public Language () {}

    public Language(String lngCode, String lngName) {
        this.lngCode = lngCode;
        this.lngName = lngName;
    }

    public String getLngCode() {
        return lngCode;
    }

    public void setLngCode(String lngCode) {
        this.lngCode = lngCode;
    }

    public String getLngName() {
        return lngName;
    }

    public void setLngName(String lngName) {
        this.lngName = lngName;
    }

    @Override
    public int compareTo(Language language) {
        return lngName.compareTo(language.getLngName());
    }
}
