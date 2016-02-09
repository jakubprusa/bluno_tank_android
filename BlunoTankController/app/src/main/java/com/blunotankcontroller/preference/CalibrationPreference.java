package com.blunotankcontroller.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.blunotankcontroller.App;
import com.blunotankcontroller.R;
import com.blunotankcontroller.orientation.calibration.GyroscopeCalibrationTask;
import com.blunotankcontroller.orientation.calibration.IGyroscopeCalibrationListener;

/**
 * Created by lubos on 9.2.16.
 */
public class CalibrationPreference extends Preference implements IGyroscopeCalibrationListener {

    private Context mContext;

    private GyroscopeCalibrationTask mCalibrationTask;

    private boolean mCalibrationRunning = false;

    public CalibrationPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public CalibrationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalibrationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalibrationPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!mCalibrationRunning) {
                    mCalibrationRunning = true;
                    mCalibrationTask = new GyroscopeCalibrationTask(context);
                    mCalibrationTask.setListener(CalibrationPreference.this);
                    mCalibrationTask.execute();
                } else {
                    Toast.makeText(context, R.string.calibration_already_running, Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        updateSummary();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    public void onCalibrationStart() {
        setEnabled(false);
    }

    @Override
    public void onCalibrationProgress(int elapsed, int from) {
        setTitle(mContext.getString(R.string.calibration_progress, elapsed, from));
    }

    @Override
    public void onCalibrationFinish() {
        setEnabled(true);
        setTitle(R.string.run_calibration);
        updateSummary();
    }

    private void updateSummary() {
        setSummary(mContext.getString(R.string.calibration_errors, App.get().getSettings().getSettingGyroErrorX(), App.get().getSettings().getSettingGyroErrorY(), App.get().getSettings().getSettingGyroErrorZ()));
    }
}
