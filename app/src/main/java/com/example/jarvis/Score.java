package com.example.jarvis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class Score {
    private int scorePlayer1;
    private int scorePlayer2;
    private int playingToScore;

    private final float scoreOffset = 400;
    private final float textSize = 250;

    private Activity activity;
    private MainThread thread;

    public Score( int playingToScore, Activity parentActivity, MainThread thread){
        this.playingToScore = playingToScore;
        this.activity = parentActivity;
        this.thread = thread;
    }

    public void update(Player player1, Player player2){
        // TODO: 05/08/2020 update saved score... I need the algorithm
        // player1 has won
        if( scorePlayer1 >= playingToScore) {
            popWinnerScreen( player1.getPlayerName());
        }

        // player2 has won
        if( scorePlayer2 >= playingToScore) {
            popWinnerScreen( player2.getPlayerName());
        }
    }

    public void draw( Canvas canvas, Paint paint){
        //display scores
        paint.setTextSize( textSize);
        canvas.rotate( 90);
        canvas.drawText( "" + scorePlayer2,GameView.screenHeight / 2 - scoreOffset, -(GameView.screenWidth) + textSize, paint);
        canvas.drawText( "" + scorePlayer1, GameView.screenHeight / 2 + scoreOffset - textSize / 2, -(GameView.screenWidth) + textSize, paint);
        canvas.rotate( -90);
    }

    private void popWinnerScreen( String winner){
        Handler activityHandler = new Handler( activity.getMainLooper());
        thread.setPause( true);
        activityHandler.post( () -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable( false);
            builder.setMessage( winner + " has won!");

            builder.setPositiveButton("Home", (dialog, which) -> {
                activity.finish();
            });

            builder.setNegativeButton("Play again", ( dialog, which) -> {
                scorePlayer2 = 0;
                scorePlayer1 = 0;
                thread.setPause( false);
            });

            builder.show();
        });
    }

    public void increaseScorePlayer1(){
        scorePlayer1++;
    }

    public void increaseScorePlayer2(){
        scorePlayer2++;
    }

    public void resetScore(){
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
    }
}
