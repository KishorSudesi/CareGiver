package com.hdfc.listeners;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.hdfc.libs.Utils;

/**
 * Created by Suhail on 8/18/2016.
 */

public class OnSwipeTouchListener implements View.OnTouchListener {

    private ListView list;
    private GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context ctx, ListView list) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        //Context context = ctx;
        this.list = list;
    }



   /* public OnSwipeTouchListener() {
        super();
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void onSwipeRight(int pos) {
        Utils.log(String.valueOf(pos), "onSwipeRight 0");
        //Do what you want after swiping left to right

    }

    public void onSwipeLeft(int pos) {
        Utils.log(String.valueOf(pos), "onSwipeLeft 0");
        //Do what you want after swiping right to left
    }

 /*   public void onSwipeTop(int pos) {
        Utils.log(String.valueOf(pos), "onSwipeTop 0");
    }

    public void onSwipeBottom(int pos) {
        Utils.log(String.valueOf(pos), "onSwipeBottom 0");
    }*/

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 30;
        private static final int SWIPE_VELOCITY_THRESHOLD = 30;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        private int getPosition(MotionEvent e1) {
            return list.pointToPosition((int) e1.getX(), (int) e1.getY());
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) >
                            SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight(getPosition(e1));
                        } else {
                            onSwipeLeft(getPosition(e1));
                        }
                    }
                    result = true;
                }
               /* else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) >
                        SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom(getPostion(e1));
                    } else {
                        onSwipeTop(getPostion(e1));
                    }
                }*/
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

    }
}
