package a535apps.scripturememory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import a535apps.scripturememory.MemoryPassage;

/**
 * Created by Patrick on 12/26/2017.
 */

public class DataSource {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mdbHelper;

    public DataSource(Context context) {
        this.mContext = context;
        mdbHelper = new DBHelper(mContext);
        mDatabase = mdbHelper.getWritableDatabase();
    }

    public void open(){
        mDatabase = mdbHelper.getWritableDatabase();
    }

    public void close(){
        mdbHelper.close();
    }

    public void insertPsg(MemoryPassage psg){
        ContentValues values = TransformData.psgToValues(psg);
        mDatabase.insert(SavedPsgsTable.TABLE_SAVEDPSGS, null, values);
    }

    public long getSavedPsgCount () {
        return DatabaseUtils.queryNumEntries(mDatabase, SavedPsgsTable.TABLE_SAVEDPSGS);
    }
}
