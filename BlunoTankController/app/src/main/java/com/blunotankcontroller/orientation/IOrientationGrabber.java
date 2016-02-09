package com.blunotankcontroller.orientation;

public interface IOrientationGrabber {
    void startTracking(float[] data);

    void stopTracking();


    float getRotationX();

    float getRotationY();

    float getRotationZ();

    float getRotationW();

    float getSensitivity();

    void reset();

    void setSensitivity(float val);

    boolean isLandscape();

    void setandscape(boolean isLandscape);
}

