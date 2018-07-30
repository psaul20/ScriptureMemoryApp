package com.scripturememory.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.adapters.SavedPsgAdapter;

public class MemoryExercise extends AppCompatActivity {

    boolean blnSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        //Retrieve appropriate passage from intent parcelable
        final MemoryPassage psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);
        if (psg != null) {
            TextView txtPsgRef = findViewById(R.id.txtPsgRef);
            TextView txtPsgText = findViewById(R.id.txtPsgText);
            Button btnCancel = findViewById(R.id.btnCancel);
            Button btnSuccess = findViewById(R.id.btnSuccess);
            Button btnFail = findViewById(R.id.btnFail);


            txtPsgRef.setText(psg.getPsgReference());

            txtPsgText.setText(psg.getText());

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btnSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    blnSuccess = true;
                    endExercise(psg);
                }
            });

            btnFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    blnSuccess = false;
                    endExercise(psg);
                }
            });
        }



    }

    //Could possibly move this and other algorithm-related methods into it's own class
    private void endExercise(MemoryPassage psg) {

        //First exercise case - start interval regardless of success or failure
        if (psg.getCurrentSeq() == 0){
            psg.setCurrentSeq(1);
            psg.setPrevSeq(0);
            psg.setLastExerc(System.currentTimeMillis());
            finish();
        } else {
            long lngTimePassed = System.currentTimeMillis() - psg.getLastExerc();
            if(blnSuccess){
                //Underdue case - do nothing
                if(lngTimePassed < psg.getCurrentSeq() * 3600000L) {
                    finish();
                } else{
                    //Exercise ready case - increase fibonacci interval
                    int intHolder = psg.getPrevSeq();
                    psg.setPrevSeq(psg.getCurrentSeq());
                    psg.setCurrentSeq(psg.getCurrentSeq() + intHolder);
                    psg.setLastExerc(System.currentTimeMillis());
                    updateDB(psg);
                    finish();
                }
            } else {
                //Overdue case - reduce interval
                if(lngTimePassed > psg.getNextExerc() + psg.getPrevSeq() * 3600000L) {
                    int intHolder = psg.getCurrentSeq();
                    psg.setCurrentSeq(psg.getPrevSeq());
                    psg.setPrevSeq(intHolder - psg.getPrevSeq());
                    psg.setLastExerc(System.currentTimeMillis());
                    updateDB(psg);
                    finish();
                } else {
                    //Normal failure case - maintain interval
                    psg.setLastExerc(System.currentTimeMillis());
                    updateDB(psg);
                    finish();
                }
            }
        }

    }

    private void updateDB (MemoryPassage psg) {
        SavedPsgsService mSavedPsgsService = new SavedPsgsService(this);
        mSavedPsgsService.openDb();
        mSavedPsgsService.updatePsg(psg);
        mSavedPsgsService.closeDb();
    }

}


