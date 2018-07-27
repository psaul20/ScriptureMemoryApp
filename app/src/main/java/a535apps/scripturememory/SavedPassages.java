package a535apps.scripturememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import a535apps.scripturememory.database.DataSource;

public class SavedPassages extends AppCompatActivity {

    private RecyclerView rcvSavedVerses;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager llmVerseLayout;
    private List<MemoryPassage> lstSavedPsgs = new ArrayList<>();
    private DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_psgs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddPsg = (FloatingActionButton) findViewById(R.id.fabAddVerse);
        fabAddPsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SavedPassages.this, AddVerse.class));
                Snackbar.make(view, "Passage Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {

            //Automatically calls onCreate method if the database hasn't been created
            mDataSource = new DataSource(this);
            mDataSource.open();

            getSavedPsgs();

            for (MemoryPassage psg:lstSavedPsgs) {
                psg.setNextExerc();
                psg.setExercMsg();
            }

            //Sort items coming due sooner first
            Collections.sort(lstSavedPsgs, new Comparator<MemoryPassage>() {
                public int compare(MemoryPassage one, MemoryPassage other){
                    return Long.compare(one.getLngNextExerc(), other.getLngNextExerc());
                }
            });

            //Create RecyclerView to be populated
            rcvSavedVerses = (RecyclerView) findViewById(R.id.rcvSavedVerses);

            //Used for performance gains if list item layout will not change based on addition of new items
            rcvSavedVerses.setHasFixedSize(true);

            //Declare and specify layout manager for RecyclerView
            llmVerseLayout = new LinearLayoutManager(this);
            rcvSavedVerses.setLayoutManager(llmVerseLayout);

            //Declare and specify adapter for RecyclerView. See SavedPsgAdapter Class
            adapter = new SavedPsgAdapter(this, lstSavedPsgs);
            rcvSavedVerses.setAdapter(adapter);

        }
        catch (Exception e)
        {
            //Exception Handling
            e.printStackTrace();
        }

    }

    protected void getSavedPsgs (){
        try {
            long numItems = mDataSource.getSavedPsgCount();
            if (numItems > 0 ){
                lstSavedPsgs = mDataSource.getAllItems();
            }
            else {
                //Display View to entice user to add verses
                System.out.println("NO SAVED PASSAGES");
            }
        }
        catch (Exception e){
            //Exception Handling
        }
    }

    //This is a lifecycle method for when the orientation changes on the device
    //or anything else happens which interrupts the activity
    //closing prevents database leaks
    @Override
    protected void onPause(){
        super.onPause();
        mDataSource.close();
    }

    //Lifecycle method for interrupts
    @Override
    protected void onResume(){
        super.onResume();
        mDataSource.open();
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
