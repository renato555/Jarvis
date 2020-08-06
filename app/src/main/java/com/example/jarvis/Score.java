package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Score {
    private int scorePlayer1;
    private int scorePlayer2;

    private int playingToScore;
    private float scoreOffset = 400;
    private float textSize = 250;

    public Score( int startingScore1, int startingScore2, int playingToScore){
        scorePlayer1 = startingScore1;
        scorePlayer2 = startingScore2;
        this.playingToScore = playingToScore;
    }

    public void update(){
        // TODO: 05/08/2020 update saved score... I need the algorithm
        if( scorePlayer1 >= playingToScore) {}// player1 has won
        if( scorePlayer2 >= playingToScore) {}// player2 has won
    }

    public void draw( Canvas canvas, Paint paint){
        //display scores
        paint.setTextSize( textSize);
        canvas.rotate( 90);
        canvas.drawText( "" + scorePlayer2,GameView.screenHeight / 2 - scoreOffset, -(GameView.screenWidth) + textSize, paint);
        canvas.drawText( "" + scorePlayer1, GameView.screenHeight / 2 + scoreOffset - textSize / 2, -(GameView.screenWidth) + textSize, paint);
        canvas.rotate( -90);
    }

    public void increaseScorePlayer1(){
        scorePlayer1++;
    }

    public void increaseScorePlayer2(){
        scorePlayer2++;
    }
}
