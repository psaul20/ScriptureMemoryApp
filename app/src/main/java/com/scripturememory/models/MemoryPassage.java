package com.scripturememory.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Patrick on 8/17/2017.
 * Parcelable implementation for moving object between activities
 */

public class MemoryPassage implements Parcelable {

    private String strPsgID;
    private String strTranslation;
    private String strBook;
    private int intChapter;
    private int intStartVerse;
    private int intEndVerse;
    private String strText;

    public MemoryPassage(){
        if(strPsgID == null) {
            strPsgID = UUID.randomUUID().toString();
        }
    }

    public MemoryPassage(String translation, String book, int chapter, int startVerse, int endVerse, String text) {
        if(strPsgID == null) {
            strPsgID = UUID.randomUUID().toString();
        }
        this.strTranslation = translation;
        this.strBook = book;
        this.intChapter = chapter;
        this.intStartVerse = startVerse;
        this.intEndVerse = endVerse;
        this.strText = text;
    }

    public String getPsgID() {
        return strPsgID;
    }

    public void setPsgID(String strPsgID) {
        this.strPsgID = strPsgID;
    }

    public String getTranslation() {
        return strTranslation;
    }

    public void setTranslation(String Translation) {
        strTranslation = Translation;
    }

    public String getBook() {
        return strBook;
    }

    public void setBook(String Book) {
        strBook = Book;
    }

    public int getChapter() {
        return intChapter;
    }

    public void setChapter(int Chapter) {
        intChapter = Chapter;
    }

    public int getStartVerse() {
        return intStartVerse;
    }

    public void setStartVerse(int StartVerse) {
        intStartVerse = StartVerse;
    }

    public int getEndVerse() {
        return intEndVerse;
    }

    public void setEndVerse(int EndVerse) {
        intEndVerse = EndVerse;
    }

    public String getText() {
        return strText;
    }

    public void setText(String strText) {
        this.strText = strText;
    }

    public String getPsgReference () {
        String strPsgRef;
        if (intEndVerse == intStartVerse) {
            strPsgRef = strBook + " " + Integer.toString(intChapter) + ":" + Integer.toString(intEndVerse);
        }

        else {
            strPsgRef = strBook + " " + Integer.toString(intChapter) + ":" + Integer.toString(intStartVerse) + "-" + Integer.toString(intEndVerse);
        }

        return strPsgRef;
    }

    //could be useful for creating uniform file names when passages are written to JSON files
    //May not be used now that we are using SQLite for storage
    public String toFileName() {
        String strFileName = getPsgReference().replaceAll(" ","_");
        strFileName.replaceAll(":",".");
        return strFileName;
    }



    //Parcelable required methods


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.strPsgID);
        dest.writeString(this.strTranslation);
        dest.writeString(this.strBook);
        dest.writeInt(this.intChapter);
        dest.writeInt(this.intStartVerse);
        dest.writeInt(this.intEndVerse);
        dest.writeString(this.strText);
    }

    protected MemoryPassage(Parcel in) {
        this.strPsgID = in.readString();
        this.strTranslation = in.readString();
        this.strBook = in.readString();
        this.intChapter = in.readInt();
        this.intStartVerse = in.readInt();
        this.intEndVerse = in.readInt();
        this.strText = in.readString();
    }

    public static final Parcelable.Creator<MemoryPassage> CREATOR = new Parcelable.Creator<MemoryPassage>() {
        @Override
        public MemoryPassage createFromParcel(Parcel source) {
            return new MemoryPassage(source);
        }

        @Override
        public MemoryPassage[] newArray(int size) {
            return new MemoryPassage[size];
        }
    };
}
