package com.scripturememory.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.scripturememory.R;
import com.scripturememory.fragments.SettingsFragment;
import com.scripturememory.fragments.SettingsLanguageFragment;

public class Settings extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);

        if (findViewById(R.id.settings_fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Add the fragment to the 'settings_language_fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.settings_fragment_container, new SettingsFragment())
                    .commit();

            PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);
        }

    }


}
