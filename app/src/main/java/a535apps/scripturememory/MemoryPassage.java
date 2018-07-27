package a535apps.scripturememory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import static java.lang.Math.abs;

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
    private long lngLastExerc;
    private long lngNextExerc;
    private int intCurrentSeq;
    private int intPrevSeq;
    private String strExercMsg;

    public MemoryPassage(){
        if(strPsgID == null) {
            strPsgID = UUID.randomUUID().toString();
        }
    }

    //Update this constructor for database
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

    //Getters and Setters
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

    public long getLngLastExerc() {
        return lngLastExerc;
    }

    public void setLngLastExerc(long lngLastExerc) {
        this.lngLastExerc = lngLastExerc;
    }

    public long getLngNextExerc() {
        return lngNextExerc;
    }

    public void setLngNextExerc(long lngNextExerc) {
        this.lngNextExerc = lngNextExerc;
    }

    public int getIntCurrentSeq() {
        return intCurrentSeq;
    }

    public void setIntCurrentSeq(int intCurrentSeq) {
        this.intCurrentSeq = intCurrentSeq;
    }

    public int getIntPrevSeq() {
        return intPrevSeq;
    }

    public void setIntPrevSeq(int intPrevSeq) {
        this.intPrevSeq = intPrevSeq;
    }

    public String getStrExercMsg() {
        return strExercMsg;
    }

    public void setStrExercMsg(String strExercMsg) {
        this.strExercMsg = strExercMsg;
    }


    //Custom methods
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

    public void setNextExerc (){

        //Indicates newly added verse
        if (intCurrentSeq == 0){
            lngNextExerc = System.currentTimeMillis();
        }

        else {
            //Find time past since last exercise
            long lngTimeDiff = System.currentTimeMillis() - lngLastExerc;
            //Convert intCurrentSeq from hours into Millis, subtract to find diff between time past since last
            //exercise and next exercise time
            lngNextExerc = intCurrentSeq * 3600000L - lngTimeDiff;
        }
    }

    public void setExercMsg(){
        //Ready to Exercise within 10 minute window
        if (lngNextExerc <= System.currentTimeMillis() + 600000L && lngNextExerc >= System.currentTimeMillis()) {
            strExercMsg = "Ready to Exercise!";
        }

        //Not ready to exercise yet
        else if (lngNextExerc > System.currentTimeMillis() + 600000L){
            strExercMsg = "Next Exercise in " + calcDueDate(lngNextExerc);
        }

        //Past due on exercise
        else if (lngNextExerc < System.currentTimeMillis() ){
            strExercMsg = "Next Exercise in " + calcDueDate(lngNextExerc);
        }
    }

    private String calcDueDate (long lngMillis){
        String strReturn = "";

        //Years
        if (lngMillis / 31540000000L > 0 || lngMillis / 31540000000L < 0){
            strReturn = Long.toString(abs(lngMillis / 31540000000L)) + " years";
        }

        //Months
        else if (lngMillis / 2628000000L > 0 || lngMillis / 2628000000L < 0){
            strReturn = Long.toString(abs(lngMillis / 2628000000L)) + " months";
        }

        //Weeks
        else if (lngMillis / 604800000L > 0 || lngMillis / 604800000L < 0){
            strReturn = Long.toString(abs(lngMillis / 604800000L)) + " weeks";
        }

        //Days
        else if (lngMillis / 86400000L > 0 || lngMillis / 86400000L < 0){
            strReturn = Long.toString(abs(lngMillis / 86400000L)) + " days";
        }

        //Hours
        else if (lngMillis / 3600000L > 0 || lngMillis / 3600000L < 0){
            strReturn = Long.toString(abs(lngMillis / 3600000L)) + " hours";
        }

        //Minutes
        else if (lngMillis / 60000L > 0 || lngMillis / 60000L < 0){
            strReturn = Long.toString(abs(lngMillis / 60000L)) + " minutes";
        }

        //Seconds
        else if (lngMillis / 1000L > 0 || lngMillis / 1000L < 0){
            strReturn = Long.toString(abs(lngMillis / 1000L)) + " seconds";
        }

        return strReturn;
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
