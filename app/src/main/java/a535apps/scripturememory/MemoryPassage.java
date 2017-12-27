package a535apps.scripturememory;

import java.util.UUID;

/**
 * Created by Patrick on 8/17/2017.
 * Parcelable implementation for
 */

public class MemoryPassage{

    private String strPsgID;
    private String strTranslation;
    private String strBook;
    private int intChapter;
    private int intStartVerse;
    private int intEndVerse;
    private String strText;

    public MemoryPassage(){
            strPsgID = UUID.randomUUID().toString();
    }

    public String getPsgID() {
        return strPsgID;
    }

    public void setPsgID(String strPsgID) {
        this.strPsgID = strPsgID;
    }

    public String getTranslation(){return strTranslation;}
    public void setTranslation(String Translation) {strTranslation = Translation;}

    public String getBook(){return strBook;}
    public void setBook(String Book) {strBook = Book;}

    public int getChapter(){return intChapter;}
    public void setChapter(int Chapter) {
        intChapter = Chapter;}

    public int getStartVerse(){return intStartVerse;}
    public void setStartVerse(int StartVerse) {
        intStartVerse = StartVerse;}

    public int getEndVerse(){return intEndVerse;}
    public void setEndVerse(int EndVerse) {
        intEndVerse = EndVerse;}

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
    public String toFileName (){
        String strFileName = getPsgReference().replaceAll(" ","_");
        strFileName.replaceAll(":",".");
        return strFileName;
    }

}
