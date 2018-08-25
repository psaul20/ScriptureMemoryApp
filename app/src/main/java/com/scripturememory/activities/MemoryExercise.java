package com.scripturememory.activities;

import android.icu.lang.UProperty;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scripturememory.algorithm.ExerciseScheduling;
import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.adapters.SavedPsgAdapter;

public class MemoryExercise extends AppCompatActivity {

    boolean blnSuccess;
    boolean blnUpdateDb;
    MemoryPassage psg;
    long lngExercStartMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

       lngExercStartMillis = System.currentTimeMillis();

        //Retrieve appropriate passage from intent parcelable
        psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);

        if (psg != null) {
            TextView txtPsgRef = findViewById(R.id.txtPsgRef);
            TextView txtPsgText = findViewById(R.id.txtPsgText);
            Button btnSuccess = findViewById(R.id.btnSuccess);
            Button btnFail = findViewById(R.id.btnFail);


            txtPsgRef.setText(psg.getPsgReference());

            txtPsgText.setText(psg.getText());

            btnSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    blnSuccess = true;
                    blnUpdateDb = ExerciseScheduling.calcNextExercise(psg, lngExercStartMillis, blnSuccess);
                    endExercise(blnUpdateDb);
                }
            });

            btnFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    blnSuccess = false;
                    blnUpdateDb = ExerciseScheduling.calcNextExercise(psg, lngExercStartMillis, blnSuccess);
                    endExercise(blnUpdateDb);
                }
            });
        }



    }

    //Could possibly move this and other algorithm-related methods into its own class
    private void endExercise(boolean updateDB) {

        if (updateDB) {
            SavedPsgsService mSavedPsgsService = new SavedPsgsService(this);
            mSavedPsgsService.openDb();
            mSavedPsgsService.updatePsg(psg);
            mSavedPsgsService.closeDb();
        }

        finish();
    }
}


