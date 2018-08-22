package com.scripturememory.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private boolean blnSuccess;
    private boolean blnUpdateDb;
    private MemoryPassage psg;
    private long lngExercStartMillis;
    private List<String> lstWords;
    private TextView txtPsgRef;
    private TextView txtPsgTextPreview;
    private TextView txtPsgTextFull;
    private Button btnSuccess;
    private Button btnFail;
    private Button btnShowMe;
    private ViewGroup rootLayout;
    private static final String SHOW_ME_PRESSED = "showMePressed";
    private boolean blnShowMePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        blnShowMePressed = false;

        lngExercStartMillis = System.currentTimeMillis();

        //Retrieve parceled passage from intent parcelable
        psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);

        txtPsgRef = findViewById(R.id.txtPsgRef);
        txtPsgTextPreview = findViewById(R.id.txtPsgTextPreview);
        txtPsgTextFull = findViewById(R.id.txtPsgTextFull);
        btnSuccess = findViewById(R.id.btnSuccess);
        btnFail = findViewById(R.id.btnFail);
        btnShowMe = findViewById(R.id.btnShowMe);

        rootLayout = findViewById(R.id.root_layout);

        lstWords = Arrays.asList(psg.getText().split(" "));

        txtPsgRef.setText(psg.getPsgReference());

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

        //Retrieve saved instance state for orientation changes
        if (savedInstanceState != null) {
            blnShowMePressed = savedInstanceState.getBoolean(SHOW_ME_PRESSED);
        }
        
        if (!blnShowMePressed) {
            txtPsgTextPreview.setText(getPsgTxtPreview(lstWords));

            btnShowMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Records all layout changes and applies auto transitions to them on
                    //redraw
                    TransitionManager.beginDelayedTransition(rootLayout);
                    showFullText();

                    blnShowMePressed = true;

                }

            });
        }

        else {
            showFullText();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean(SHOW_ME_PRESSED, blnShowMePressed);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //If it has been over 5 minutes, end the activity and return to savedpsgs
        if (System.currentTimeMillis() > lngExercStartMillis + 300000L){
            endExercise(false);
        }
    }

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

    private void showFullText(){
        btnShowMe.setVisibility(View.GONE);
        btnSuccess.setVisibility(View.VISIBLE);
        btnFail.setVisibility(View.VISIBLE);

        txtPsgTextFull.setText(getFullPsgTxt(lstWords));
        txtPsgTextPreview.setVisibility(View.INVISIBLE);
        txtPsgTextFull.setVisibility(View.VISIBLE);
    }


}


