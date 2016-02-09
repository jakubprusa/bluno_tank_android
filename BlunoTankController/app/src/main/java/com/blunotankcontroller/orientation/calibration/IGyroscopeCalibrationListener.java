package com.blunotankcontroller.orientation.calibration;

/**
 * Created by lubos on 9.2.16.
 */
public interface IGyroscopeCalibrationListener {
    void onCalibrationStart();

    void onCalibrationProgress(int elapsed, int from);

    void onCalibrationFinish();
}
