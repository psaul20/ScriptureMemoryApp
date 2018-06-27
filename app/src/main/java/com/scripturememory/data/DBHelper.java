package com.scripturememory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Patrick on 12/26/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    //this should be changed to reflect the name of the app?
    public static final String DB_FILE_NAME = "ScriptureMemory.db";
    //this should be changed if the DB is ever changed
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    //Called the first time the application is opened
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SavedPsgsTable.SQL_CREATE);
    }

    //Called the first time the application is opened after it has been upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Store current data in a JSON file
        //Drop database, recreate, import data back in
        //Increment DB Version
    }
}
