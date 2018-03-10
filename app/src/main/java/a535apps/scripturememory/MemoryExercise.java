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

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import a535apps.scripturememory.MemoryPassage;

public class MemoryExercise extends AppCompatActivity {

    ArrayList<String> psgWords;

    FlexboxLayout fbLayout;

    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        fbLayout = (FlexboxLayout) findViewById(R.id.fbLayout);

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

            //should this logic be moved to createpsgtextviews method?
            for(int i = 0; i < psgWords.size(); i++ ){
                for(String checkWord : testWords){
                    if(psgWords.get(i).toUpperCase().equals(checkWord.toUpperCase()) ){
                        psgWords.set(i,"+");
                    }
                }
            }
        }

        CreatePsgTextViews(psgWords);

    }

    protected void CreatePsgTextViews (ArrayList<String> words){

        for (String word : words){
            txtView = new TextView(this);

            txtView.setTextSize(30);
            FlexboxLayout.MarginLayoutParams lp = new FlexboxLayout.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            lp.setMargins(100,100,100,0);

            txtView.setLayoutParams(lp);

            if(!word.equals("+")){
                txtView.setText(word);
            }
            else {
                txtView.setText(R.string.blank_space);
            }

            fbLayout.addView(txtView);

        }
    }
}


