package com.blunotankcontroller.orientation;

import android.content.Context;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOrientationGrabber implements IOrientationGrabber {
    protected SensorManager mSensorManager;

    protected boolean mIsReady = false;

    protected boolean mIsLandscape = true;

    protected float mRotationX;    // pitch

    protected float mRotationY;    // yaw (azimuth)

    protected float mRotationZ;    // roll

    protected float mRotationW;    // w of quat

    protected float mSensitivity = 2.0f;

    protected List<IOrientationChangeListener> mOrientationListeners = new ArrayList<>();

    public boolean registerListener(IOrientationChangeListener listener) {
        if(mOrientationListeners.contains(listener)) return false;
        mOrientationListeners.add(listener);
        return true;
    }

    public boolean unregisterListener(IOrientationChangeListener listener) {
        return mOrientationListeners.remove(listener);
    }

    public AbstractOrientationGrabber(Context context, boolean isLandscape) {
        mIsLandscape = isLandscape;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public abstract void startTracking(float[] data);

    public abstract void stopTracking();


    public abstract float getRotationX();

    public abstract float getRotationY();

    public abstract float getRotationZ();

    public abstract float getRotationW();

    public void reset() {
        mRotationX = 0.0f;
        mRotationY = 0.0f;
        mRotationZ = 0.0f;
        mRotationW = 0.0f;
    }

    public float getSensitivity() {
        return mSensitivity;
    }

    public void setSensitivity(float val) {
        mSensitivity = val;
    }

    public boolean isLandscape() {
        return mIsLandscape;
    }

    public void setandscape(boolean isLandscape) {
        mIsLandscape = isLandscape;
    }
}
