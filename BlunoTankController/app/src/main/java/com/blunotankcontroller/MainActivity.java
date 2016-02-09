package com.blunotankcontroller;

import android.os.Bundle;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blunotankcontroller.orientation.IOrientationChangeListener;
import com.blunotankcontroller.orientation.OrientationEvent;
import com.blunotankcontroller.orientation.RelativeOrientationGrabber;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BlunoLibrary implements IOrientationChangeListener {

    @InjectView(R.id.buttonScan)
    protected Button mButtonScan;

    @InjectView(R.id.buttonSerialSend)
    protected Button mButtonSerialSend;

    @InjectView(R.id.resetSensor)
    protected Button mButtonResetSensor;

    @InjectView(R.id.serialSendText)
    protected EditText mSerialSendText;

    @InjectView(R.id.serialReceivedText)
    protected TextView mSerialReceivedText;

    @InjectView(R.id.orientationText)
    protected TextView mOrientationText;

    @InjectView(R.id.goText)
    protected TextView mGoText;

    @InjectView(R.id.buttonGo)
    protected Button mGoButton;

    @InjectView(R.id.buttonSettings)
    protected Button mSettingsButton;

    protected long mLastTimestamp = 0;

    protected final long CONTROL_UPDATE_PERIOD_MS = 100;

    protected RelativeOrientationGrabber mOrientationGrabber;

    protected boolean mControlEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        onCreateProcess();                                                        //onCreate Process by BlunoLibrary
        serialBegin(9600);                                                    //set the Uart Baudrate on BLE chip to 115200

        mButtonSerialSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                serialSend(mSerialSendText.getText().toString());                //send the data to the BLUNO
                mSerialSendText.setText("");
            }
        });

        mButtonScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
            }
        });

        mGoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mControlEnabled = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mControlEnabled = false;
                    sendResetMessage();
                }
                return false;
            }
        });

        mOrientationGrabber = new RelativeOrientationGrabber(this, true);
        mButtonResetSensor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOrientationGrabber.reset();
                mSerialReceivedText.setText("");
                sendResetMessage();
            }
        });

        mSettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendResetMessage() {
        serialSend("[");
        serialSend("[");
        serialSend("0");
        serialSend("0");
        serialSend("0");
        serialSend("0");
    }

    protected void onResume() {
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();                                                        //onResume Process by BlunoLibrary
        mOrientationGrabber.startTracking(new float[] { App.get().getSettings().getSettingGyroErrorX(), App.get().getSettings().getSettingGyroErrorY(), App.get().getSettings().getSettingGyroErrorZ() });
        mOrientationGrabber.registerListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary
        mOrientationGrabber.unregisterListener(this);
        mOrientationGrabber.stopTracking();
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
    }

    @Override
    public void onConnectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {                                            //Four connection state
            case isConnected:
                mButtonScan.setText("Connected");
                break;
            case isConnecting:
                mButtonScan.setText("Connecting");
                break;
            case isToScan:
                mButtonScan.setText("Scan");
                break;
            case isScanning:
                mButtonScan.setText("Scanning");
                break;
            case isDisconnecting:
                mButtonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
        mSerialReceivedText.append(theString);
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
    }

    private boolean shouldApplyControl() {
        long currentTime = System.currentTimeMillis();
        if (mLastTimestamp == 0) {
            mLastTimestamp = currentTime;
        } else if (currentTime - mLastTimestamp > CONTROL_UPDATE_PERIOD_MS) {
            mLastTimestamp = currentTime;
            return true;
        }
        return false;
    }

    private int clampIntToBorder(double val, double border) {
        if (val > border) {
            return (int) border;
        } else if (val < -border) {
            return (int) -border;
        }
        return (int) val;
    }

    @Override
    public void onOrientationChanged(OrientationEvent event) {
        if (!shouldApplyControl()) return;

        StringBuilder builder = new StringBuilder();
        builder.append("X = " + event.x + "\n");
        builder.append("Y = " + event.y + "\n");
        builder.append("Z = " + event.z + "\n");
        mOrientationText.setText(builder.toString());

        final double BORDER = 127.0;
        double speedX = event.x * BORDER / 90.0;
        double speedZ = event.z * BORDER / 90.0;

        double angle = Math.toDegrees(Math.atan2(speedZ, speedX));
        double magnitute = Math.sqrt(speedZ * speedZ + speedX + speedX);

        int tempMotor1 = clampIntToBorder(speedX - speedZ, BORDER);
        char motor1Direction = (char) (tempMotor1 < 0 ? '1' : '0');
        char motor1Speed = (char) (tempMotor1 < 0 ? -tempMotor1 : tempMotor1);

        int tempMotor2 = clampIntToBorder(speedX + speedZ, BORDER);
        char motor2Direction = (char) (tempMotor2 < 0 ? '1' : '0');
        char motor2Speed = (char) (tempMotor2 < 0 ? -tempMotor2 : tempMotor2);

        if (mControlEnabled) {
            serialSend("[");
            serialSend("[");
            serialSend(String.valueOf(motor1Direction));
            serialSend(String.valueOf(motor1Speed));
            serialSend(String.valueOf(motor2Direction));
            serialSend(String.valueOf(motor2Speed));
        }

        StringBuilder resultBuilder = new StringBuilder();
        if (mControlEnabled) {
            resultBuilder.append("Angle: " + (int) angle + "\n");
            resultBuilder.append((event.x > 0 ? "Forward" : "Backward") + ", " + (event.z < 0 ? "Left" : "Right") + "\n");
            resultBuilder.append("Motor 1 - Dir: " + motor1Direction + " Speed: " + (byte) motor1Speed + "\n");
            resultBuilder.append("Motor 2 - Dir: " + motor2Direction + " Speed: " + (byte) motor2Speed + "\n");
        } else {
            resultBuilder.append("Control inactive.\n");
        }
        mGoText.setText(resultBuilder.toString());
    }
}