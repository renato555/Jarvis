package com.example.jarvis;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private final ChipNavigationBar navigationBar;

    private int[] idDictionary = { R.id.colleague, R.id.weather, R.id.home, R.id.toDo, R.id.calendar};

    public OnSwipeTouchListener(Context ctx, ChipNavigationBar navigationBar) {
        gestureDetector = new GestureDetector( ctx, new GestureListener());
        this.navigationBar = navigationBar;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent( motionEvent);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if( Math.abs( diffX) > Math.abs( diffY)){
                if( Math.abs( diffX) > SWIPE_THRESHOLD && Math.abs( velocityX) > SWIPE_VELOCITY_THRESHOLD){
                    if( diffX > 0){
                        onSwipeRight();
                    }else{
                        onSwipeLeft();
                    }
                    result = true;
                }else if( Math.abs( diffY) > SWIPE_THRESHOLD && Math.abs( velocityY) > SWIPE_VELOCITY_THRESHOLD){
                    if( diffY > 0){
                        onSwipeBottom();
                    }else{
                        onSwipeTop();
                    }
                    result = true;
                }
            }
            return result;
        }
    }

    public void onSwipeRight(){
        int currentItemIndex = extractIndex( navigationBar.getSelectedItemId());
        if(  currentItemIndex <= 0) return;

        navigationBar.setItemSelected( idDictionary[ currentItemIndex - 1], true);
    }

    public void onSwipeLeft(){
        int currentItemIndex = extractIndex( navigationBar.getSelectedItemId());
        if( currentItemIndex >= 4) return;

        navigationBar.setItemSelected( idDictionary[ currentItemIndex + 1], true);
    }

    public void onSwipeTop(){

    }

    public void onSwipeBottom(){

    }

    private int extractIndex( int id){
        if( id == R.id.calendar) return 4;
        if( id == R.id.colleague) return 0;
        if( id == R.id.home) return 2;
        if( id == R.id.toDo) return 3;
        if( id == R.id.weather) return 1;

        return 3;
    }
}
