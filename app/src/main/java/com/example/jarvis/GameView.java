package com.example.jarvis;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback( this);
        thread = new MainThread( getHolder(), this);
        setFocusable( true);
    }

    public void update(){

    }

    @Override
    public void draw( Canvas canvas){
        super.draw( canvas);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        thread.setIsRunning( true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while( retry){
            try{
                thread.setIsRunning( false);
                thread.join();
            }catch ( InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }
}
