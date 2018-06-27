package com.scripturememory.models;

/**
 * Created by Tyler on 12/28/17.
 */

public class Book {

    // damId is XXXYYYT
    // XXX = language code, YYY = version code
    // T = O (old testimate) or N (new testimate)
    private String damId;
    private String bookId;
    private String bookName;
    private int numChapters;

    public String getDamId() {
        return damId;
    }

    public void setDamId(String damId) {
        this.damId = damId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getNumChapters() {
        return numChapters;
    }

    public void setNumChapters(int numChapters) {
        this.numChapters = numChapters;
    }
}
