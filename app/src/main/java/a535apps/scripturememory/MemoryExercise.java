package a535apps.scripturememory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import a535apps.scripturememory.MemoryPassage;

public class MemoryExercise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_exercise);

        MemoryPassage psg = getIntent().getExtras().getParcelable(SavedPsgAdapter.PSG_KEY);
        if (psg != null)
        {
            TextView txtRef = (TextView) findViewById(R.id.txtPsgRef);
            txtRef.setText(psg.getPsgReference());

            TextView txtText = (TextView) findViewById(R.id.txtPsgText);
            txtText.setText(psg.getText());
        }


    }

}
