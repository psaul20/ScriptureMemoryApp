package a535apps.scripturememory;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//For documentation go to https://github.com/google/flexbox-layout
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class MemoryExercise extends AppCompatActivity {

    ArrayList<String> lstDropWords;
    ArrayList<String> lstDragWords;

    FlexboxLayout fblDropZone;
    FlexboxLayout fblDragZone;

    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        //Connect xml views to java code
        fblDropZone = (FlexboxLayout) findViewById(R.id.fblDropZone);
        fblDragZone = (FlexboxLayout) findViewById(R.id.fblDragZone);

        //Determine which words will be removed to be dragged and dropped onto
        ArrayList<String> testWords = new ArrayList<>(1);
        testWords.add("god");
        testWords.add("world");
        testWords.add("perish");

        //Instantiate word holder arrays
        lstDropWords = new ArrayList<>();
        lstDragWords = new ArrayList<>();

        //Retrieve selected passage from intent parcelable
        MemoryPassage psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);
        if (psg != null)
        {
            //Convert all words in the psg string to a list
            for(String word : psg.getText().trim().split(" ")){
                lstDropWords.add(word);
            }

            //Check each word in testwords list against words in the retrieved psg
            for(int i = 0; i < lstDropWords.size(); i++ ){
                for(String checkWord : testWords){
                    //If there is a match, replace the word with drag and drop word indicator
                    if(lstDropWords.get(i).toUpperCase().equals(checkWord.toUpperCase()) ){
                        lstDragWords.add(lstDropWords.get(i));
                        lstDropWords.set(i,"+");
                    }
                }
            }
        }

        CreatePsgDropViews(lstDropWords);

        CreatePsgDragViews(lstDragWords);

    }

    protected void CreatePsgDropViews (ArrayList<String> words) {

        //For each word in psg, create a TextView and add it to the flexboxlayout
        for (String word : words) {

            //Create TextView
            txtView = new TextView(this);

            //Customize TextView properties
            txtView.setTextSize(30);

            //Create and customize Flexbox child view Params
            FlexboxLayout.MarginLayoutParams lp = new FlexboxLayout.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            lp.setMargins(100, 100, 100, 0);

            //Set LayoutParams to TxtView
            txtView.setLayoutParams(lp);

            //If the word is a special word, replace with blank space to indicate droppable area
            if (!word.equals("+")) {
                txtView.setText(word);
            } else {
                txtView.setText(R.string.blank_space);
            }

            //Add txtView to FlexBox
            fblDropZone.addView(txtView);

        }
    }

    protected void CreatePsgDragViews (ArrayList<String> words){

        for (String word : words){
            txtView = new TextView(this);

            txtView.setTextSize(30);
            FlexboxLayout.MarginLayoutParams lp = new FlexboxLayout.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            lp.setMargins(100,100,100,0);

            txtView.setLayoutParams(lp);

            txtView.setText(word);
            txtView.setTag(word);
            txtView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event){

                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        ClipData cdDrag = ClipData.newPlainText(v.getTag().toString(), v.getTag().toString());

                        View.DragShadowBuilder dragShadow = new MemExDragShadowBuilder(txtView);

                        v.startDrag(cdDrag,  // the data to be dragged
                                dragShadow,  // the drag shadow builder
                                null,      // no need to use local data
                                0          // flags (not currently used, set to 0)
                        );
                        return true;
                    }

                    else
                        return false;




                }
            });

            fblDragZone.addView(txtView);

        }
    }
}


