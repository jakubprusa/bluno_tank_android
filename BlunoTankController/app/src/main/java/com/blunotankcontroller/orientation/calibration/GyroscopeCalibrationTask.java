package com.blunotankcontroller.orientation.calibration;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.blunotankcontroller.App;


public class GyroscopeCalibrationTask extends AsyncTask<Void, Integer, String> {
    private Context mContext;

    private GyroscopeCalibrator mGyroCalibrator;

    private static final long CALIBRATION_PERIOD_IN_SECONDS = 60;

    private IGyroscopeCalibrationListener mListener;


    public GyroscopeCalibrationTask(Context context) {
        super();
        this.mContext = context;
        mGyroCalibrator = new GyroscopeCalibrator(context, CALIBRATION_PERIOD_IN_SECONDS);
    }

    public void setListener(IGyroscopeCalibrationListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mGyroCalibrator.startTracking(null);
        mListener.onCalibrationStart();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        for (int i = 0; i < CALIBRATION_PERIOD_IN_SECONDS; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }
        return "You are at PostExecute";
    }

    @Override
    protected void onProgressUpdate(Integer... a) {
        mListener.onCalibrationProgress(a[0], (int)CALIBRATION_PERIOD_IN_SECONDS);
        Log.d(GyroscopeCalibrationTask.class.getName(), "Progress update " + a[0] + "/" + CALIBRATION_PERIOD_IN_SECONDS);
    }

    @Override
    protected void onPostExecute(String result) {
        mGyroCalibrator.stopTracking();

        float elapsedTime = mGyroCalibrator.getElapsedTime();

        float additiveErrorPerNanoSecondX = mGyroCalibrator.getRotationX() / elapsedTime;
        float additiveErrorPerNanoSecondY = mGyroCalibrator.getRotationY() / elapsedTime;
        float additiveErrorPerNanoSecondZ = mGyroCalibrator.getRotationZ() / elapsedTime;

        Log.d("Result", "Time: " + elapsedTime);
        Log.d("Result", "XXX = " + additiveErrorPerNanoSecondX);
        Log.d("Result", "YYY = " + additiveErrorPerNanoSecondY);
        Log.d("Result", "ZZZ = " + additiveErrorPerNanoSecondZ);

        App.get().getSettings().setSettingGyroErrorX(additiveErrorPerNanoSecondX);
        App.get().getSettings().setSettingGyroErrorY(additiveErrorPerNanoSecondY);
        App.get().getSettings().setSettingGyroErrorZ(additiveErrorPerNanoSecondZ);

        mListener.onCalibrationFinish();

        StringBuilder builder = new StringBuilder();
        builder.append("Calibration finished:\n");
        builder.append("Xe = " + additiveErrorPerNanoSecondX + " deg/s\n");
        builder.append("Ye = " + additiveErrorPerNanoSecondY + " deg/s\n");
        builder.append("Ze = " + additiveErrorPerNanoSecondZ + " deg/s\n");
        Toast.makeText(mContext, builder.toString(), Toast.LENGTH_LONG).show();
    }
}
