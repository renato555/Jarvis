package com.example.jarvis;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread{
    private SurfaceHolder surfaceHolder;
    private GameView gameView;

    private boolean isRunning;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView){
        super();

        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        while( isRunning){
            canvas = null;

            try{
                canvas = surfaceHolder.lockCanvas();
                synchronized ( surfaceHolder){
                    gameView.update();
                    gameView.draw( canvas);
                }
            }catch( Exception e){
                e.printStackTrace();
            }finally{
                if( canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost( canvas);
                    }catch ( Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setIsRunning( boolean isRunning) { this.isRunning = isRunning;}
}
