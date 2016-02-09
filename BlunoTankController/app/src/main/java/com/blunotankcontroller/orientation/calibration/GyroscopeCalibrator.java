package com.blunotankcontroller.orientation.calibration;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GyroscopeCalibrator implements SensorEventListener {
    private SensorManager mSensorManager;

    private Sensor mGyroscopeSensor;

    private float mRotationX;

    private float mRotationY;

    private float mRotationZ;

    private float mRotationW;

    private static final float NS2S = 1.0f / 1000000000.0f;

    protected static final float EPSILON = 0.000000001f;

    private final float[] mDeltaRotationVector = new float[4];

    private float mTimestamp = 0;

    private float mElapsedTime = 0;

    private float mCalibrationPeriod = 0;


    public GyroscopeCalibrator(Context context, float periodInSeconds) {
        this.mCalibrationPeriod = periodInSeconds;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void startTracking(float[] data) {
        mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopTracking() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int acc) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE) return;
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        if (mTimestamp != 0) {
            final float deltaTimeInSeconds = (event.timestamp - mTimestamp) * NS2S;

            if (mElapsedTime >= mCalibrationPeriod) return;

            mElapsedTime += deltaTimeInSeconds;

            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            float thetaOverTwo = omegaMagnitude * deltaTimeInSeconds / 2.0f;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);

            mDeltaRotationVector[0] = sinThetaOverTwo * axisX;
            mDeltaRotationVector[1] = sinThetaOverTwo * axisY;
            mDeltaRotationVector[2] = sinThetaOverTwo * axisZ;
            mDeltaRotationVector[3] = cosThetaOverTwo;

            mRotationX += Math.toDegrees(mDeltaRotationVector[0]);
            mRotationY += Math.toDegrees(mDeltaRotationVector[1]);
            mRotationZ += Math.toDegrees(mDeltaRotationVector[2]);
            mRotationW += Math.toDegrees(mDeltaRotationVector[3]);
        }

        mTimestamp = event.timestamp;
    }

    public float getRotationY() {
        return mRotationY;
    }

    public float getRotationX() {
        return mRotationX;
    }

    public float getRotationZ() {
        return mRotationZ;
    }

    public float getRotationW() {
        return mRotationW;
    }

    public float getElapsedTime() {
        return mElapsedTime;
    }
}


