package com.example.jarvis;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread{
    private final SurfaceHolder surfaceHolder;
    private GameView gameView;

    private boolean isRunning;
    private boolean pause;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView){
        super();

        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        while( isRunning){
            while( !pause){
                canvas = null;
                try{
                    canvas = surfaceHolder.lockCanvas();
                    synchronized ( surfaceHolder){
                        //game heartbeat
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
    }

    public void setIsRunning( boolean isRunning) { this.isRunning = isRunning;}

    public void setPause( boolean pause){ this.pause = pause;}
}
