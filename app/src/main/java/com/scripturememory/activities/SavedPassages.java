package com.scripturememory.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.scripturememory.Notifications.NotificationReceiver;
import com.scripturememory.algorithm.ExerciseScheduling;
import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.adapters.SavedPsgAdapter;

public class SavedPassages extends AppCompatActivity {

    private RecyclerView rcvSavedPsgs;
    private List<MemoryPassage> lstSavedPsgs = new ArrayList<>();
    private SavedPsgsService savedPsgsService;
    private final String CHANNEL_ID = "TestChannel";
    private Notification ntfReadyForReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_psgs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddPsg = findViewById(R.id.fabAddVerse);
        fabAddPsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SavedPassages.this, AddPsg.class));
            }
        });


        savedPsgsService = new SavedPsgsService(this);
        savedPsgsService.openDb();
        lstSavedPsgs = savedPsgsService.getAllPsgs();

        //Create RecyclerView to be populated
        rcvSavedPsgs = findViewById(R.id.rcvSavedPsgs);

        //Used for performance gains if list item layout will not change based on addition of new items
        rcvSavedPsgs.setHasFixedSize(true);

        //Declare and specify layout manager for RecyclerView
        rcvSavedPsgs.setLayoutManager(new LinearLayoutManager(this));

        //Declare and specify adapter for RecyclerView. See SavedPsgAdapter Class
        RecyclerView.Adapter adapter = new SavedPsgAdapter(this, lstSavedPsgs);
        rcvSavedPsgs.setAdapter(adapter);

        if (!lstSavedPsgs.isEmpty()) {

            long lngCurrentTimeMillis = System.currentTimeMillis();

            for (MemoryPassage psg : lstSavedPsgs) {
                ExerciseScheduling.buildExercMsg(psg, lngCurrentTimeMillis);
            }

            //Sort items coming due sooner first
            Collections.sort(lstSavedPsgs, new Comparator<MemoryPassage>() {
                public int compare(MemoryPassage one, MemoryPassage other) {
                    return Long.compare(one.getNextExerc(), other.getNextExerc());
                }
            });




            //If Next Exercise is scheduled for a later time
            if (lngCurrentTimeMillis < lstSavedPsgs.get(0).getNextExerc()) {
                //Create NotificationChannel
                createNotificationChannel();
                ntfReadyForReview = buildNotification();
                scheduleNotification(ntfReadyForReview, lstSavedPsgs.get(0).getNextExerc());

            }

        }

        else {
            rcvSavedPsgs.setVisibility(View.GONE);
            TextView txtNoPsgs = findViewById(R.id.txtNoPsgs);
            txtNoPsgs.setVisibility(View.VISIBLE);
        }
    }


    //This is a lifecycle method for when the orientation changes on the device
    //or anything else happens which interrupts the activity
    //closing prevents database leaks
    @Override
    protected void onPause() {
        super.onPause();
        savedPsgsService.closeDb();
    }

    //Lifecycle method for interrupts
    @Override
    protected void onResume() {
        super.onResume();
        savedPsgsService.openDb();
        lstSavedPsgs = savedPsgsService.getAllPsgs();

        if (!lstSavedPsgs.isEmpty()) {

            long lngCurrentTimeMillis = System.currentTimeMillis();

            for (MemoryPassage psg : lstSavedPsgs) {
                ExerciseScheduling.buildExercMsg(psg, lngCurrentTimeMillis);
            }

            //Sort items coming due sooner first
            Collections.sort(lstSavedPsgs, new Comparator<MemoryPassage>() {
                public int compare(MemoryPassage one, MemoryPassage other) {
                    return Long.compare(one.getNextExerc(), other.getNextExerc());
                }
            });

            //Declare and specify adapter for RecyclerView. See SavedPsgAdapter Class
            RecyclerView.Adapter adapter = new SavedPsgAdapter(this, lstSavedPsgs);
            rcvSavedPsgs.setAdapter(adapter);

            if (lngCurrentTimeMillis < lstSavedPsgs.get(0).getNextExerc()) {
                ntfReadyForReview = buildNotification();
                scheduleNotification(ntfReadyForReview, lstSavedPsgs.get(0).getNextExerc());
            }
        }

        else {
            rcvSavedPsgs.setVisibility(View.GONE);
            TextView txtNoPsgs = findViewById(R.id.txtNoPsgs);
            txtNoPsgs.setVisibility(View.VISIBLE);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification (){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification")
                .setContentText("Bible passages are ready for review!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification mNotification = mBuilder.build();

        return mNotification;

    }


    private void scheduleNotification(Notification notification, long NotificationTime){
        Intent mNotificationIntent = new Intent(this, NotificationReceiver.class);
        mNotificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        mNotificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC, NotificationTime, mPendingIntent);
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
