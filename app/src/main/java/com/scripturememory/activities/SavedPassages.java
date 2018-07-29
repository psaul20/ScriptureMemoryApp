package com.scripturememory.activities;

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
import java.util.List;

import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.adapters.SavedPsgAdapter;
import com.scripturememory.data.SavedPsgsDao;

public class SavedPassages extends AppCompatActivity {

    private RecyclerView rcvSavedVerses;
    private List<MemoryPassage> lstSavedPsgs = new ArrayList<>();
    private SavedPsgsService savedPsgsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_verses);

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

        savedPsgsService = new SavedPsgsService(this);
        savedPsgsService.openDb();
        lstSavedPsgs = savedPsgsService.getAllPsgs();
        rcvSavedVerses = (RecyclerView) findViewById(R.id.rcvSavedVerses);

        //Used for performance gains if list item layout will not change based on addition of new items
        rcvSavedVerses.setHasFixedSize(true);

        //Declare and specify layout manager for RecyclerView
        rcvSavedVerses.setLayoutManager(new LinearLayoutManager(this));

        //Declare and specify adapter for RecyclerView. See SavedVerseAdapter Class
        RecyclerView.Adapter adapter = new SavedPsgAdapter(this, lstSavedPsgs);
        rcvSavedVerses.setAdapter(adapter);

    }

    //This is a lifecycle method for when the orientation changes on the device
    //or anything else happens which interrupts the activity
    //closing prevents database leaks
    @Override
    protected void onPause(){
        super.onPause();
        savedPsgsService.closeDb();
    }

    //Lifecycle method for interrupts
    @Override
    protected void onResume(){
        super.onResume();
        savedPsgsService.openDb();
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