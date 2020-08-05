package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Score {
    private int scorePlayer1;
    private int scorePlayer2;

    private int playingToScore;

    public Score( int startingScore1, int startingScore2, int playingToScore){
        scorePlayer1 = startingScore1;
        scorePlayer2 = startingScore2;
        this.playingToScore = playingToScore;
    }

    public void update(){
        // TODO: 05/08/2020 do smth after finishing
        if( scorePlayer1 >= playingToScore) {}// player1 has won
        if( scorePlayer2 >= playingToScore) {}// player2 has won
    }

    public void draw( Canvas canvas, Paint paint){
        // TODO: 05/08/2020 display scores
    }

    public void increaseScorePlayer1(){
        scorePlayer1++;
    }

    public void increaseScorePlayer2(){
        scorePlayer2++;
    }
}
