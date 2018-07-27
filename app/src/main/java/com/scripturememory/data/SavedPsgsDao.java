package com.scripturememory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.scripturememory.models.MemoryPassage;
import com.scripturememory.helpers.TransformData;

/**
 * Created by Patrick on 12/26/2017.
 * This Dao interacts holds the methods that interact with the database
 */
public class SavedPsgsDao {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mdbHelper;

    private Logger logger = Logger.getLogger(getClass().toString());

    public SavedPsgsDao(Context context) {
        this.mContext = context;
        mdbHelper = new SavedPsgsHelper(mContext);
    }

    public void open(){
        mDatabase = mdbHelper.getWritableDatabase();
        logger.info("DB OPENED");
    }

    public void close(){
        mdbHelper.close();
        logger.info("DB CLOSED");
    }

    public void insertPsg(MemoryPassage psg){
        ContentValues values = TransformData.psgToValues(psg);
        mDatabase.insert(SavedPsgsTable.TABLE_SAVEDPSGS, null, values);
    }

    public long getSavedPsgCount() {
        return DatabaseUtils.queryNumEntries(mDatabase, SavedPsgsTable.TABLE_SAVEDPSGS);
    }

    public List<MemoryPassage> getAllPsgs() {
        List<MemoryPassage> lstSavedPsgs = new ArrayList<>();

        //Equivalent to Java resultset class
        //Sorts and filters can be implemented using the additional parameters
        //See "Filter and sort Data lesson" in Local Data Storage video for implementing filters
        Cursor cursor = mDatabase.query(SavedPsgsTable.TABLE_SAVEDPSGS, SavedPsgsTable.ALL_COLUMNS, null, null, null, null, null);

        while(cursor.moveToNext()){
            MemoryPassage psg = new MemoryPassage();
            psg.setPsgID(cursor.getString(cursor.getColumnIndex(SavedPsgsTable.COLUMN_ID)));
            psg.setTranslation(cursor.getString(cursor.getColumnIndex(SavedPsgsTable.COLUMN_TRANSLATION)));
            psg.setBook(cursor.getString(cursor.getColumnIndex(SavedPsgsTable.COLUMN_BOOK)));
            psg.setChapter(cursor.getInt(cursor.getColumnIndex(SavedPsgsTable.COLUMN_CHAPTER)));
            psg.setStartVerse(cursor.getInt(cursor.getColumnIndex(SavedPsgsTable.COLUMN_STARTVERSE)));
            psg.setEndVerse(cursor.getInt(cursor.getColumnIndex(SavedPsgsTable.COLUMN_ENDVERSE)));
            psg.setText(cursor.getString(cursor.getColumnIndex(SavedPsgsTable.COLUMN_TEXT)));
            lstSavedPsgs.add(psg);
        }
        return lstSavedPsgs;
    }
}
