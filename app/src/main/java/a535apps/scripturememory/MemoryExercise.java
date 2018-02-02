package a535apps.scripturememory;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import a535apps.scripturememory.MemoryPassage;

public class MemoryExercise extends AppCompatActivity {

/*    LinearLayout llPsgLine1;
    LinearLayout llPsgLine2;
    LinearLayout llPsgLine3;
    LinearLayout llPsgLine4;*/

    ConstraintLayout clPsgDisplay = (ConstraintLayout) findViewById(R.id.clPsgDisplay);
    ConstraintSet csPsgDisplay = new ConstraintSet();

    ConstraintLayout clWordBankDisplay = (ConstraintLayout) findViewById(R.id.clWordBankDisplay);
    ConstraintSet csWordBankDisplay = new ConstraintSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        /*llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine1);
        llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine2);
        llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine3);
        llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine4);*/

        ArrayList<String> testWords = new ArrayList<>(1);
        testWords.add("god");
        testWords.add("world");
        testWords.add("perish");

        ArrayList<String> psgWords = new ArrayList<>();


        MemoryPassage psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);
        if (psg != null)
        {
            //does this work?
            for(String word : psg.getText().trim().split(" ")){
                psgWords.add(word);
            }

            for(String word : psgWords){
                for(String checkWord : testWords){
                    if(word.toUpperCase().equals(checkWord.toUpperCase()) ){
                        word = "+";
                    }
                }
            }

            CreatePsgTextViews(psgWords);


        }

    }

    protected void CreatePsgTextViews (ArrayList<String> words){

        //Does this work?
        clPsgDisplay.measure(0,0);
        int intRemainingDps = clPsgDisplay.getMeasuredWidth();
        int intTopMargin = 10;
        TextView txtPrevView = new TextView(this);
        boolean blnNewLine = true;

        for (String word : words){
            TextView txtView = new TextView(this);
            if(!word.equals("+")){
                txtView.setText(word);
            }
            else {
                txtView.setText(R.string.blank_space);
            }

            clPsgDisplay.addView(txtView);
            csPsgDisplay.clone(clPsgDisplay);
            csPsgDisplay.constrainHeight(txtView.getId(), ConstraintSet.WRAP_CONTENT);
            csPsgDisplay.constrainWidth(txtView.getId(), ConstraintSet.WRAP_CONTENT);

            //calculate remaining DPs
            //if insufficient DPs remain, create horizontal chain with existing TextViews

            if(blnNewLine){
                csPsgDisplay.connect(txtView.getId(),ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 5);
            }
            else {
                csPsgDisplay.connect(txtView.getId(),ConstraintSet.LEFT,
                        txtPrevView.getId(), ConstraintSet.RIGHT, 5);
            }

            csPsgDisplay.connect(txtView.getId(),ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.TOP, intTopMargin);

            //Does this work?
            txtView.measure(0,0);
            intRemainingDps = intRemainingDps - (txtView.getMeasuredWidth() + 5);

            if (intRemainingDps <= 0) {
                blnNewLine = true;
                intTopMargin += 10;
                intRemainingDps = clPsgDisplay.getMeasuredWidth();
            }
            else
                blnNewLine = false;

            txtPrevView = txtView;
        }

    }

}
