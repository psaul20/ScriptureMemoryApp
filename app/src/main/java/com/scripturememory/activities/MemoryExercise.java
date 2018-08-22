package com.scripturememory.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.scripturememory.algorithm.ExerciseScheduling;
import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.adapters.SavedPsgAdapter;

import java.util.Arrays;
import java.util.List;

public class MemoryExercise extends AppCompatActivity {

    boolean blnSuccess;
    boolean blnUpdateDb;
    MemoryPassage psg;
    long lngExercStartMillis;
    List<String> lstWords;
    TextView txtPsgRef;
    TextView txtPsgTextPreview;
    TextView txtPsgTextFull;
    Button btnSuccess;
    Button btnFail;
    Button btnShowMe;
    ViewGroup rootLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

       lngExercStartMillis = System.currentTimeMillis();

        //Retrieve parceled passage from intent parcelable
        psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);

        if (psg != null) {
            txtPsgRef = findViewById(R.id.txtPsgRef);
            txtPsgTextPreview = findViewById(R.id.txtPsgTextPreview);
            txtPsgTextFull = findViewById(R.id.txtPsgTextFull);
            btnSuccess = findViewById(R.id.btnSuccess);
            btnFail = findViewById(R.id.btnFail);
            btnShowMe = findViewById(R.id.btnShowMe);

            rootLayout = findViewById(R.id.root_layout);

            lstWords= Arrays.asList(psg.getText().split(" "));

            txtPsgRef.setText(psg.getPsgReference());
            txtPsgTextPreview.setText(getPsgTxtPreview(lstWords));

            btnShowMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){

                    //Records all layout changes and applies auto transitions to them on
                    //redraw
                    TransitionManager.beginDelayedTransition(rootLayout);

                    btnShowMe.setVisibility(View.GONE);
                    btnSuccess.setVisibility(View.VISIBLE);
                    btnFail.setVisibility(View.VISIBLE);

                    txtPsgTextFull.setText(getFullPsgTxt(lstWords));
                    txtPsgTextPreview.setVisibility(View.INVISIBLE);
                    txtPsgTextFull.setVisibility(View.VISIBLE);

                }

            });

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
    private void endExercise(boolean UpdateDB) {

        if (UpdateDB) {
            SavedPsgsService mSavedPsgsService = new SavedPsgsService(this);
            mSavedPsgsService.openDb();
            mSavedPsgsService.updatePsg(psg);
            mSavedPsgsService.closeDb();
        }

        finish();
    }

    private String getPsgTxtPreview(List<String> lstWords){

        StringBuilder stringBuilder = new StringBuilder("");

        for (int i = 0; i < 2; i++) {
            stringBuilder.append(lstWords.get(i));
            stringBuilder.append(" ");
        }
        stringBuilder.append(lstWords.get(2));
        stringBuilder.append("...");

        return stringBuilder.toString();

    }

    private String getFullPsgTxt (List<String> lstWords) {
        StringBuilder stringBuilder = new StringBuilder("");

        for (String word: lstWords) {
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().trim();
    }


}


