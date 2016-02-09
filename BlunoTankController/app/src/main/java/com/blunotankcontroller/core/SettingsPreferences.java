package com.blunotankcontroller.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by lubos on 9.2.16.
 */
public class SettingsPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String SETTING_GYRO_ERROR_X                = "setting_gyro_error_x";
    public static final String SETTING_GYRO_ERROR_Y                = "setting_gyro_error_Y";
    public static final String SETTING_GYRO_ERROR_Z                = "setting_gyro_error_Z";

    private Context mContext;
    private final SharedPreferences mSettings;

    public SettingsPreferences(Context context) {
        mContext = context;
        mSettings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSettingGyroErrorX(float errorX) {
        mSettings.edit().putFloat(SETTING_GYRO_ERROR_X, errorX).apply();
    }

    public float getSettingGyroErrorX() {
        return mSettings.getFloat(SETTING_GYRO_ERROR_X, 0.0f);
    }

    public void setSettingGyroErrorY(float errorY) {
        mSettings.edit().putFloat(SETTING_GYRO_ERROR_Y, errorY).apply();
    }

    public float getSettingGyroErrorY() {
        return mSettings.getFloat(SETTING_GYRO_ERROR_Y, 0.0f);
    }

    public void setSettingGyroErrorZ(float errorZ) {
        mSettings.edit().putFloat(SETTING_GYRO_ERROR_Z, errorZ).apply();
    }

    public float getSettingGyroErrorZ() {
        return mSettings.getFloat(SETTING_GYRO_ERROR_Z, 0.0f);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
