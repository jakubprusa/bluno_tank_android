package com.blunotankcontroller;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by lubos on 9.2.16.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
