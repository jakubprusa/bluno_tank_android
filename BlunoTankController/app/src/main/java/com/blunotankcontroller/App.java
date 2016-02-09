package com.blunotankcontroller;

import android.app.Application;

import com.blunotankcontroller.core.SettingsPreferences;

/**
 * Created by lubos on 9.2.16.
 */
public class App extends Application {

    private static App sInstance;

    public static App get() {
        return sInstance;
    }

    private SettingsPreferences mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        mSettings = new SettingsPreferences(this);
        sInstance = this;
    }

    public SettingsPreferences getSettings() {
        return mSettings;
    }
}
