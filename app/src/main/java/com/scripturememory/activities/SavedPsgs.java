package com.scripturememory.activities;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.scripturememory.Interfaces.ItemClickListener;
import com.scripturememory.fragments.DeletePsgDialogFragment;
import com.scripturememory.notifications.NotificationReceiver;
import com.scripturememory.algorithm.ExerciseScheduling;
import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.adapters.SavedPsgAdapter;

import static com.scripturememory.adapters.SavedPsgAdapter.PSG_KEY;

public class SavedPsgs extends AppCompatActivity
        implements
        ItemClickListener, DeletePsgDialogFragment.DeletePassageDialogListener {

    private RecyclerView rcvSavedPsgs;
    private List<MemoryPassage> lstSavedPsgs = new ArrayList<>();
    private SavedPsgsService savedPsgsService;
    private final String CHANNEL_ID = "ExerciseReminder";
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
                startActivity(new Intent(SavedPsgs.this, AddPsg.class));
            }
        });

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
        SavedPsgAdapter adapter = new SavedPsgAdapter(this, lstSavedPsgs);
        rcvSavedPsgs.setAdapter(adapter);
        adapter.setClickListener(this);

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

        } else {
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
            NotificationManager mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {

        Intent mIntent = new Intent(this, SavedPsgs.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bible Memory")
                .setContentText("Bible passages are ready for review!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(mPendingIntent)
                .setAutoCancel(true);

        Notification mNotification = mBuilder.build();

        return mNotification;

    }


    private void scheduleNotification(Notification notification, long NotificationTime) {
        Intent mIntent = new Intent(this, NotificationReceiver.class);
        mIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        mIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, NotificationTime, mPendingIntent);

    }

    //Click handling for Recyclerview
    MemoryPassage clickedPsg;

    @Override
    public void onClick(View v, int position) {

        clickedPsg = lstSavedPsgs.get(position);

        //Get specific holder for row that was clicked
        SavedPsgAdapter.ViewHolder mHolder = (SavedPsgAdapter.ViewHolder) rcvSavedPsgs.findViewHolderForAdapterPosition(position);

        //Delete button pressed
        if (v.getId() == R.id.btnDeletePsg) {

            //Show dialog
            showDeleteDialog(clickedPsg.getPsgReference(), clickedPsg.getPsgID());

        }

        //Delete button visible, but another view pressed
        else if (mHolder.btnDeletePsg.getVisibility() == View.VISIBLE) {
            mHolder.btnDeletePsg.setVisibility(View.GONE);
        }

        //Delete button not visible, view pressed
        else {
            //Open new exercise activity, pass psg along with intent
            Intent intent = new Intent(this, MemoryExercise.class);
            intent.putExtra(PSG_KEY, clickedPsg);
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v, int position) {

        //Get delete specific holder for row that was clicked
        SavedPsgAdapter.ViewHolder mHolder = (SavedPsgAdapter.ViewHolder) rcvSavedPsgs.findViewHolderForAdapterPosition(position);

        //Make delete button appear
        if (mHolder.btnDeletePsg.getVisibility() == View.GONE) {
            mHolder.btnDeletePsg.setVisibility(View.VISIBLE);
        }

        //Hide delete button
        else {
            mHolder.btnDeletePsg.setVisibility(View.GONE);
        }

        return true;
    }

    //Dialog box Handling

    private void showDeleteDialog(String PsgRef, String PsgId) {
        DialogFragment dialog = DeletePsgDialogFragment.newInstance(PsgRef, PsgId);
        dialog.show(getFragmentManager(), "DeletePsgDialogFragment");
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog, String PsgId) {
        //Delete passage, call onResume to reset recyclerview
        savedPsgsService.deletePsg(PsgId);

        //Maybe need to use notifyItemRemoved method for adapter instead
        onResume();
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog){
        dialog.dismiss();
    }

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

