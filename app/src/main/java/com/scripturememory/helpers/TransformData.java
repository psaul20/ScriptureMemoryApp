package com.scripturememory.helpers;

import android.content.ContentValues;

import com.scripturememory.models.MemoryPassage;
import com.scripturememory.data.SavedPsgsTable;

/**
 * Created by Patrick on 12/27/2017.
 * To be used for transforming data from Java Object into database-ready format and vice versa
 */

public class TransformData {
    public static ContentValues psgToValues (MemoryPassage psg){
        ContentValues values = new ContentValues();

        values.put(SavedPsgsTable.COLUMN_ID, psg.getPsgID());
        values.put(SavedPsgsTable.COLUMN_TRANSLATION, psg.getTranslation());
        values.put(SavedPsgsTable.COLUMN_BOOK, psg.getBook());
        values.put(SavedPsgsTable.COLUMN_CHAPTER, psg.getChapter());
        values.put(SavedPsgsTable.COLUMN_STARTVERSE, psg.getStartVerse());
        values.put(SavedPsgsTable.COLUMN_ENDVERSE, psg.getEndVerse());
        values.put(SavedPsgsTable.COLUMN_TEXT, psg.getText());

        return values;

    }
}
