package com.scripturememory.data;

import android.content.Context;

import com.scripturememory.activities.MemoryExercise;
import com.scripturememory.models.MemoryPassage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Acts as a handler between the SavedPsgsDao and the Activities
 */
public class SavedPsgsService {

    private SavedPsgsDao savedPsgsDao;
    private Logger logger = Logger.getLogger(getClass().toString());

    public SavedPsgsService(Context context) {
        savedPsgsDao = new SavedPsgsDao(context);
    }

    public void openDb() {
        savedPsgsDao.open();
    }

    public void closeDb() {
        savedPsgsDao.close();
    }

    public void insertPsg(MemoryPassage psg) {
        savedPsgsDao.insertPsg(psg);
    }

    public void updatePsg(MemoryPassage psg) {savedPsgsDao.updatePsg(psg);}

    public long getSavedPsgCount() {
        return savedPsgsDao.getSavedPsgCount();
    }

    public List<MemoryPassage> getAllPsgs() {
        List<MemoryPassage> memoryPassages = new ArrayList<>();
        try {
            memoryPassages = savedPsgsDao.getAllPsgs();
            if (memoryPassages.size() <= 0){
                logger.info("No Passages Found");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return memoryPassages;
    }

}
