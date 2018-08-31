package com.scripturememory.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.scripturememory.R;
import com.scripturememory.fragments.SettingsBibleVersionFragment;
import com.scripturememory.fragments.SettingsBibleVersionFragment.OnBibleVersionSelectedListener;

public class SettingsBibleVersion extends AppCompatActivity implements OnBibleVersionSelectedListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_longlist_fragment);

        if (findViewById(R.id.settings_longlist_fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout and pass in args if any
            SettingsBibleVersionFragment bibleVersionFragment = SettingsBibleVersionFragment.newInstance(getIntent().getExtras());

            // Add the fragment to the 'settings_language_fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.settings_longlist_fragment_container, bibleVersionFragment)
                    .commit();

            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = preferences.edit();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Listener for fragments messages sent to its activity
    @Override
    public void onBibleVersionSelected(String bibleVersion, int position) {
        editor.putString("bible_version", bibleVersion);
        editor.apply();
    }
}
