package com.dftaihua.dfth_threeinone.api;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by leezhiqiang on 2017/3/23.
 */

public class TouchEventHelper {
    private float mFingerX;
    private float mFingerY;
    private GestureDetector mGestureDetector;
    private boolean mInner;
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener;
    public TouchEventHelper(Context context,GestureDetector.SimpleOnGestureListener listener){
        mGestureDetector = new GestureDetector(context,mListener);
        mSimpleOnGestureListener = listener;
    }
    public void onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            mInner = false;
            mFingerX = event.getX();
            mFingerY = event.getY();
        }else if(action == MotionEvent.ACTION_UP){
            mFingerX = event.getX() - mFingerX;
            mFingerY = event.getY() - mFingerY;
            if(!mInner){
                mSimpleOnGestureListener.onSingleTapConfirmed(event);
            }
        }
    }

    private GestureDetector.SimpleOnGestureListener mListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public void onLongPress(MotionEvent e) {
            mInner = true;
            mSimpleOnGestureListener.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mInner = true;
            mSimpleOnGestureListener.onDoubleTap(e);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mInner = true;
            mSimpleOnGestureListener.onScroll(e1, e2, distanceX, distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mSimpleOnGestureListener.onFling(e1, e2, velocityX, velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

}