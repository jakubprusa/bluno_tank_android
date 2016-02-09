package com.blunotankcontroller.touch;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.blunotankcontroller.core.Pool;

public class SingleTouchGrabber implements ITouchGrabber {
    private boolean isTouched;

    private int touchX;

    private int touchY;

    private Pool<ITouchEvent> touchEventPool;

    private List<ITouchEvent> touchEvents = new ArrayList<ITouchEvent>();

    private List<ITouchEvent> touchEventsBuffer = new ArrayList<ITouchEvent>();

    private float scaleX;

    private float scaleY;

    protected List<ITouchListener> mOrientationListeners = new ArrayList<>();

    public boolean registerListener(ITouchListener listener) {
        if(mOrientationListeners.contains(listener)) return false;
        mOrientationListeners.add(listener);
        return true;
    }

    public boolean unregisterListener(ITouchListener listener) {
        return mOrientationListeners.remove(listener);
    }

    public SingleTouchGrabber(View view) {
        Pool.PoolObjectFactory<ITouchEvent> factory = new Pool.PoolObjectFactory<ITouchEvent>() {
            public ITouchEvent createObject() {
                return new ITouchEvent();
            }
        };

        touchEventPool = new Pool<ITouchEvent>(factory, 100);
        view.setOnTouchListener(this);
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            ITouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = ITouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = ITouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = ITouchEvent.TOUCH_UP;
                    isTouched = false;
                    break;
            }

            touchEvent.x = touchX = (int) (event.getX() * scaleX);
            touchEvent.y = touchY = (int) (event.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);

            for (ITouchListener listener : mOrientationListeners) {
                listener.onTouch(touchEvent);
            }

            return true;
        }
    }

    public boolean isTouchDown(int pointer) {
        if (pointer == 0) return isTouched;
        else return false;
    }

    public int getTouchX(int pointer) {
        return touchX;
    }

    public int getTouchY(int pointer) {
        return touchY;
    }

    public List<ITouchEvent> getTouchEvents() {
        synchronized (this) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++) touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }
}
