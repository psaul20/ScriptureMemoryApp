package a535apps.scripturememory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SavedVerses extends AppCompatActivity {

    private RecyclerView rcvSavedVerses;
    private RecyclerView.Adapter Adapter;
    private RecyclerView.LayoutManager lmVerseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_verses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddVerse = (FloatingActionButton) findViewById(R.id.fabAddVerse);
        fabAddVerse.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                startActivity(new Intent(SavedVerses.this, AddVerse.class));
                Snackbar.make(view, "Verse Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Dummy data array for implementing RCV
        String [] VerseRefData = {"John 3:16", "Ephesians 2:1-10"};

        rcvSavedVerses = (RecyclerView) findViewById(R.id.rcvSavedVerses);

        //Used for performance gains if list item layout will not change based on addition of new items
        rcvSavedVerses.setHasFixedSize(true);

        //Declare and specify layout manager for RecyclerView
        lmVerseLayout = new LinearLayoutManager(this);
        rcvSavedVerses.setLayoutManager(lmVerseLayout);

        //Declare and specify adapter for RecyclerView. See SavedVerseAdapter Class
        Adapter = new SavedVerseAdapter(VerseRefData);
        rcvSavedVerses.setAdapter(Adapter);


    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_verses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);*/
    }
