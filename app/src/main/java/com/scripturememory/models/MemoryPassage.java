package com.scripturememory.models;

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
    //Newly added passage Constructor
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
        this.lngLastExerc = -1L;
        this.lngNextExerc = -1L;
        this.intCurrentSeq = 0;
        this.intPrevSeq = 0;
        this.strExercMsg = null;
    }

    //Getters and Setters
    public String getPsgID() {
        return strPsgID;
    }

    public void setPsgID(String PsgID) {
        this.strPsgID = PsgID;
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

    public void setText(String Text) {
        this.strText = Text;
    }

    public long getLastExerc() {
        return lngLastExerc;
    }

    public void setLastExerc(long LastExerc) { this.lngLastExerc = LastExerc; }

    public long getNextExerc() {
        return lngNextExerc;
    }

    public void setNextExerc(long NextExerc) { this.lngNextExerc = NextExerc; }

    public int getCurrentSeq() {
        return intCurrentSeq;
    }

    public void setCurrentSeq(int CurrentSeq) {
        this.intCurrentSeq = CurrentSeq;
    }

    public int getPrevSeq() {
        return intPrevSeq;
    }

    public void setPrevSeq(int PrevSeq) {
        this.intPrevSeq = PrevSeq;
    }

    public String getExercMsg() {
        return strExercMsg;
    }

    public void setExercMsg(String ExercMsg) {
        this.strExercMsg = ExercMsg;
    }


    //Custom methods
    public String getPsgReference () {
        String strPsgRef;
        if (intEndVerse == intStartVerse) {
            strPsgRef = strBook + " " + intChapter + ":" + intEndVerse;
        } else {
            strPsgRef = strBook + " " + intChapter + ":" + intStartVerse + "-" + intEndVerse;
        }
        return strPsgRef;
    }

    public void calcNextExerc (){

        //Indicates newly added verse
        if (intCurrentSeq == 0){
            //100000L buffer added to account for discrepancies caused by processing time (not sure if this is necessary)
            setNextExerc(System.currentTimeMillis() + 100000L);
        }

        else {
            //Find time past since last exercise
            long lngTimeDiff = System.currentTimeMillis() - getLastExerc();
            //Convert intCurrentSeq from hours into Millis, subtract to find diff between time past since last
            //exercise and next exercise time
            setNextExerc((getCurrentSeq() * 3600000L) - lngTimeDiff);
        }
    }

    public void buildExercMsg(){

        long lngUpdatedNextExerc = getNextExerc();
        long lngExercWindow = getPrevSeq() * 3600000L;

        //Ready to Exercise window = previous sequence time interval
        if (lngUpdatedNextExerc <= System.currentTimeMillis() + lngExercWindow && lngUpdatedNextExerc >= System.currentTimeMillis()) {
            setExercMsg("Ready to Exercise!");
        }

        //Not ready to exercise yet
        else if (lngUpdatedNextExerc > System.currentTimeMillis() + lngExercWindow){
            setExercMsg("Next Exercise in " + calcDueDate(lngUpdatedNextExerc));
        }

        //Past due on exercise
        else {
            setExercMsg("Ready To Exercise! " + calcDueDate(lngUpdatedNextExerc) + " overdue");
        }
    }

    private String calcDueDate (long lngDueDateMillis){
        String strReturn = "";
        long lngYearMillis = 31540000000L;
        long lngMonthMillis = 2628000000L;
        long lngWeekMillis = 604800000L;
        long lngDayMillis = 86400000L;
        long lngHourMillis = 3600000L;
        long lngMinuteMillis = 60000L;
        long lngSecondMillis = 1000L;

        //Years
        if (lngDueDateMillis / lngYearMillis > 0 || lngDueDateMillis / lngYearMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngYearMillis)) + " years";
        }

        //Months
        else if (lngDueDateMillis / lngMonthMillis > 0 || lngDueDateMillis / lngMonthMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngMonthMillis)) + " months";
        }

        //Weeks
        else if (lngDueDateMillis / lngWeekMillis > 0 || lngDueDateMillis / lngWeekMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngWeekMillis)) + " weeks";
        }

        //Days
        else if (lngDueDateMillis / lngDayMillis > 0 || lngDueDateMillis / lngDayMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngDayMillis)) + " days";
        }

        //Hours
        else if (lngDueDateMillis / lngHourMillis > 0 || lngDueDateMillis / lngHourMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngHourMillis)) + " hours";
        }

        //Minutes
        else if (lngDueDateMillis / lngMinuteMillis > 0 || lngDueDateMillis / lngMinuteMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngMinuteMillis)) + " minutes";
        }

        //Seconds
        else if (lngDueDateMillis / lngSecondMillis > 0 || lngDueDateMillis / lngSecondMillis < 0){
            strReturn = Long.toString(abs(lngDueDateMillis / lngSecondMillis)) + " seconds";
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

    private MemoryPassage(Parcel in) {
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
