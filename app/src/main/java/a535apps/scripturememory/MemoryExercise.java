package a535apps.scripturememory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MemoryExercise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        //Retrieve appropriate passage from intent parcelable
        MemoryPassage psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);
        if (psg != null)
        {

        }

    }

}


