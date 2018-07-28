package com.scripturememory.data;

/**
 * Created by Patrick on 12/26/2017.
 * This contains the Saved Passages Table information
 */
public class SavedPsgsTable {
    public static final String TABLE_SAVEDPSGS = "savedPsgs";
    public static final String COLUMN_ID = "verseId";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_BOOK = "book";
    public static final String COLUMN_CHAPTER = "chapter";
    public static final String COLUMN_STARTVERSE = "startVerse";
    public static final String COLUMN_ENDVERSE = "endVerse";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LASTEXERC = "lastExerc";
    public static final String COLUMN_NEXTEXERC = "nextExerc";
    public static final String COLUMN_CURRENTSEQ = "currentSeq";
    public static final String COLUMN_PREVSEQ = "prevSeq";
    public static final String COLUMN_EXERCMSG = "exercMsg";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_TRANSLATION, COLUMN_BOOK, COLUMN_CHAPTER,
                    COLUMN_STARTVERSE, COLUMN_ENDVERSE, COLUMN_TEXT, COLUMN_LASTEXERC,
                    COLUMN_NEXTEXERC, COLUMN_CURRENTSEQ, COLUMN_PREVSEQ, COLUMN_EXERCMSG};

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_SAVEDPSGS + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_TRANSLATION + " TEXT," +
                    COLUMN_BOOK + " TEXT," +
                    COLUMN_CHAPTER + " INTEGER," +
                    COLUMN_STARTVERSE + " INTEGER," +
                    COLUMN_ENDVERSE + " INTEGER," +
                    COLUMN_TEXT + " TEXT," +
                    COLUMN_LASTEXERC + " INTEGER," +
                    COLUMN_NEXTEXERC + " INTEGER," +
                    COLUMN_CURRENTSEQ + " INTEGER," +
                    COLUMN_PREVSEQ + " INTEGER," +
                    COLUMN_EXERCMSG + " TEXT" +
                    ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_SAVEDPSGS;
}
