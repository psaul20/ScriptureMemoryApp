package a535apps.scripturememory;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import a535apps.scripturememory.MemoryPassage;

public class MemoryExercise extends AppCompatActivity {

/*    LinearLayout llPsgLine1;
    LinearLayout llPsgLine2;
    LinearLayout llPsgLine3;
    LinearLayout llPsgLine4;*/

    ArrayList<String> psgWords;

    ConstraintLayout clPsgDisplay;
    ConstraintSet csPsgDisplay;

    ConstraintLayout clWordBankDisplay;
    ConstraintSet csWordBankDisplay;

    int intRemainingDps;
    int intTopMargin;
    TextView txtView;
    TextView txtPrevView;
    boolean blnNewLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        clPsgDisplay = (ConstraintLayout) findViewById(R.id.clPsgDisplay);
        csPsgDisplay = new ConstraintSet();

        clWordBankDisplay = (ConstraintLayout) findViewById(R.id.clWordBankDisplay);
        csWordBankDisplay = new ConstraintSet();

        /*llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine1);
        llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine2);
        llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine3);
        llPsgLine1 = (LinearLayout) findViewById(R.id.llPsgLine4);*/

        ArrayList<String> testWords = new ArrayList<>(1);
        testWords.add("god");
        testWords.add("world");
        testWords.add("perish");

        psgWords = new ArrayList<>();

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

            ViewTreeObserver vto = clPsgDisplay.getViewTreeObserver();
            vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    clPsgDisplay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int intScreenWidth  = clPsgDisplay.getMeasuredWidth();
                    int height = clPsgDisplay.getMeasuredHeight();

                    CreatePsgTextViews(psgWords, intScreenWidth);

                }
            });

        }

    }

    protected void CreatePsgTextViews (ArrayList<String> words, int screenwidth){

        //Does this work?
       /* clPsgDisplay.measure(0,0);
        int intRemainingDps = clPsgDisplay.getMeasuredWidth();*/
        intRemainingDps = screenwidth;
        intTopMargin = 10;
        blnNewLine = true;

        for (String word : words){
            txtView = new TextView(this);
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

            csPsgDisplay.applyTo(clPsgDisplay);

//            ViewTreeObserver vto2 = txtView.getViewTreeObserver();
//            vto2.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    txtPrevView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    //Does this work?
//                    intRemainingDps = intRemainingDps - (txtView.getMeasuredWidth() + 5);
//
//                    if (intRemainingDps <= 0) {
//                        blnNewLine = true;
//                        intTopMargin += 10;
//                        intRemainingDps = clPsgDisplay.getMeasuredWidth();
//                    }
//                    else
//                        blnNewLine = false;
//
//                    txtPrevView = txtView;
//                }
//            });

            intRemainingDps = intRemainingDps - (txtView.getMeasuredWidth() + 5);

            if (intRemainingDps <= 0) {
                blnNewLine = true;
                intTopMargin += 10;
                intRemainingDps = clPsgDisplay.getMeasuredWidth();
            }
            else
                blnNewLine = false;

        }
    }
}


