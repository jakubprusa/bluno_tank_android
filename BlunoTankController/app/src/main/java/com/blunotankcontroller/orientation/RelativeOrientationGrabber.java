package com.blunotankcontroller.orientation;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RelativeOrientationGrabber extends AbstractOrientationGrabber implements SensorEventListener {
    private Sensor mGyroscopeSensor;

    private float mErrorDeltaRotationXPerSecond = -0.31958725f;

    private float mErrorDeltaRotationYPerSecond = 0.23555703f;

    private float mErrorDeltaRotationZPerSecond = -0.094252236f;

    private static final float NS2S = 1.0f / 1000000000.0f;

    protected static final float EPSILON = 0.000000001f;

    private final float[] mDeltaRotationVector = new float[4];

    private float mTimestamp = 0;


    public RelativeOrientationGrabber(Context context, boolean isLandscape) {
        super(context, isLandscape);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void startTracking(float[] data) {
        mErrorDeltaRotationXPerSecond = data[0];
        mErrorDeltaRotationYPerSecond = data[1];
        mErrorDeltaRotationZPerSecond = data[2];

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
        //if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        if (mTimestamp != 0) {
            final float deltaTimeInSeconds = (event.timestamp - mTimestamp) * NS2S;

            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ); // angular speed magnitude

            //if(omegaMagnitude <= m_noiseThreshold) return; // noise??

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

            if (mIsLandscape) {
                mRotationY += Math.toDegrees(mDeltaRotationVector[0]) - (deltaTimeInSeconds * mErrorDeltaRotationXPerSecond);
                mRotationX += Math.toDegrees(mDeltaRotationVector[1]) - (deltaTimeInSeconds * mErrorDeltaRotationYPerSecond);
                mRotationZ += -Math.toDegrees(mDeltaRotationVector[2]) - (deltaTimeInSeconds * mErrorDeltaRotationZPerSecond);
                mRotationW += Math.toDegrees(mDeltaRotationVector[3]);
            } else {
                mRotationX += Math.toDegrees(mDeltaRotationVector[0]) - (deltaTimeInSeconds * mErrorDeltaRotationXPerSecond);
                mRotationY += Math.toDegrees(mDeltaRotationVector[1]) - (deltaTimeInSeconds * mErrorDeltaRotationYPerSecond);
                mRotationZ += Math.toDegrees(mDeltaRotationVector[2]) - (deltaTimeInSeconds * mErrorDeltaRotationZPerSecond);
                mRotationW += Math.toDegrees(mDeltaRotationVector[3]);
            }

//    		Log.d(RelativeOrientationGrabber.class.getName(), "----------------------------");
//			Log.d(RelativeOrientationGrabber.class.getName(), "MAG=" + omegaMagnitude);
//			Log.d(RelativeOrientationGrabber.class.getName(), "X = " + mRotationX);
//			Log.d(RelativeOrientationGrabber.class.getName(), "Y = " + mRotationY);
//			Log.d(RelativeOrientationGrabber.class.getName(), "Z = " + mRotationZ);
//			Log.d(RelativeOrientationGrabber.class.getName(), "W = " + mRotationW);

            for (IOrientationChangeListener listener : mOrientationListeners) {
                listener.onOrientationChanged(new OrientationEvent(mRotationX * mSensitivity, mRotationY * mSensitivity, mRotationZ * mSensitivity));
            }
        }

        mTimestamp = event.timestamp;
    }

    @Override
    public float getRotationX() {
        return mRotationY * mSensitivity;
    }

    @Override
    public float getRotationY() {
        return -(mRotationX * mSensitivity) + 180.0f;
    }

    @Override
    public float getRotationZ() {
        return mRotationZ * mSensitivity;
    }

    @Override
    public float getRotationW() {
        return mRotationW * mSensitivity;
    }
}


