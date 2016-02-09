package com.blunotankcontroller.touch;

import java.util.List;

import android.view.View.OnTouchListener;


public interface ITouchGrabber extends OnTouchListener 
{
    boolean isTouchDown(int pointer);
    
    int getTouchX(int pointer);
    
    int getTouchY(int pointer);
    
    List<ITouchEvent> getTouchEvents();
    
    void setScale(float scaleX, float scaleY);
}
