package com.example.jarvis;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    public static final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    private MainThread thread;
    private Activity parentActivity;

    private Player player1;
    private Player player2;
    private Ball ball;
    private Score score;

    private PongGameDatabase database;
    private DatabaseReference player2MessageRef;

    private String roomName;
    private boolean wasReset;

    private boolean isHost;
    private int mode;


    public GameView(Activity parentActivity, String roomName, boolean isHost, int mode) {
        super( parentActivity.getApplicationContext());
        getHolder().addCallback( this);

        //initialize variables
        thread = new MainThread( getHolder(), this);
        player1 = new Player( screenWidth / 2, screenHeight - Player.playerOffSet, "Renato");
        player2 = new Player( screenWidth / 2, Player.playerOffSet - Player.height, "Lovro");
        ball = new Ball();
        score = new Score( 7, parentActivity, thread, this);
        score.setScorePlayer1(0);
        score.setScorePlayer2(0);
        this.parentActivity = parentActivity;

        this.roomName = roomName;
        this.wasReset = false;
        this.isHost = isHost;
        this.mode = mode;

        if(mode == 1) {
            database = new PongGameDatabase(isHost, roomName);
            player2MessageRef = FirebaseDatabase.getInstance().getReference("rooms/" + roomName + "/message");
            setPlayer2MessageRefListener();

            if (isHost)
                player2MessageRef.setValue("");
            else player2MessageRef.setValue("joined");
        }

        setFocusable( true);
    }

    public void update() {
        if(mode == 1)
            database.update(player1, player2 ,ball, score);
        else{
            player2.update();
            ball.update(player1, player2, score);
        }
        player1.update();
        score.update( player1, player2);
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
        if( canvas != null){
            super.draw( canvas);
            Paint paint = new Paint();
            paint.setColor( Color.rgb( 255, 255, 255));

            ball.draw( canvas, paint);
            player1.draw( canvas, paint);
            player2.draw( canvas, paint);

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
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        //sometimes thread won't shut down the first time so keep trying until successful
        boolean retry = true;
        while( retry){
            try{
                thread.setIsRunning( false);
                thread.setPause( true);
                thread.join();
            }catch ( InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    private void setPlayer2MessageRefListener() {
        player2MessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!wasReset){
                    if(snapshot.getValue() instanceof String && ((String) snapshot.getValue()).equals("")){
                        thread.setPause(true);
                    }else {
                        thread.setPause(false);
                        score.resetScore();
                        wasReset = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setWasReset(boolean b){
        wasReset = b;
    }
}
