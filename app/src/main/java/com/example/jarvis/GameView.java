package com.example.jarvis;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    public static final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    private MainThread thread;

    private Player player1;
    private Player player2;
    private Ball ball;
    private Score score;

    private PongGameDatabase database;

    public GameView(Context context, String roomName) {
        super(context);
        getHolder().addCallback( this);

        //initialize variables

        thread = new MainThread( getHolder(), this);
        player1 = new Player( screenWidth / 2, screenHeight - Player.playerOffSet,true, roomName);
        player2 = new Player( screenWidth / 2, Player.playerOffSet - Player.height, false, roomName);
        ball = new Ball();
        score = new Score( 0, 0, 10);
        database = new PongGameDatabase(roomName);

        setFocusable( true);
    }

    public void update(){
        player1.update();
        player2.update();
        ball.update( player1, player2, score);
        score.update();
        new Thread(){
            @Override
            public void run() {
                database.update(player1.getPositionX(), player2.getPositionX(), ball.getPositionX(), ball.getPositionY());
            }
        }.start();
    }

    //handle multi touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for( int i = 0; i < event.getPointerCount(); ++i){
            int index = event.findPointerIndex( event.getPointerId( i));
            if( event.getY( index) >= screenHeight / 2){
                player1.wantedPosition( event.getX( index));
            }else{
                player2.wantedPosition( event.getX( index));
            }
        }
        return true;
    }

    @Override
    public void draw( Canvas canvas){
        super.draw( canvas);
        if( canvas != null){
            Paint paint = new Paint();
            paint.setColor( Color.rgb( 255, 255, 255));
            player1.draw( canvas, paint);
            player2.draw( canvas, paint);
            ball.draw( canvas, paint);
            score.draw( canvas, paint);
            canvas.drawLine( 0, screenHeight / 2, screenWidth, screenHeight /2, paint);
        }
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
        //sometimes thread won't shut down the first time so keep trying until successful
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
