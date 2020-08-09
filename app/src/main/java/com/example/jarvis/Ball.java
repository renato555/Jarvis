package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

public class Ball {
    private float positionX;
    private float positionY;

    private float velocityX;
    private float velocityY;
    private float velocityMultiplier = 1.1f;
    private boolean bounceDirection; //if true ball is going towards player1 else player2

    private float radius = (float) 0.02 * GameView.screenWidth;

    public Ball() {
        //center the ball in the screen and pick velocity
        resetBall();
    }

    public void update(Player player1, Player player2, Score score) {
        //bounce off the wall
        if (positionX + radius >= GameView.screenWidth || positionX - radius <= 0) {
            velocityX *= -1;
        }

        //bounce off player1
        float x1 = player1.getPositionX();
        float y1 = player1.getPositionY();
        if(  ( positionX >= x1 - Player.width / 2 && positionX <= x1 + Player.width / 2) && ( positionY + radius >= y1 && positionY + radius <= y1 + Player.imaginaryHeight) && bounceDirection){
            velocityY *= -1;
            velocityY *= velocityMultiplier; //speed up in y direction
            bounceDirection = false;
        }

        //bounce off player2
        float x2 = player2.getPositionX();
        float y2 = player2.getPositionY();
        if(  ( positionX >= x2 - Player.width / 2 && positionX <= x2 + Player.width / 2) && ( positionY - radius <= y2 + Player.height  && positionY - radius >= y2 + Player.height - Player.imaginaryHeight) && !bounceDirection){
            velocityY *= -1;
            velocityY *= velocityMultiplier; //speed up in y direction
            bounceDirection = true;
        }

        if (positionY >= GameView.screenHeight) {
            //player2 scored
            score.increaseScorePlayer2();
            resetBall();
        }
        if (positionY <= 0) {
            //player1 scored
            score.increaseScorePlayer1();
            resetBall();
        }

        positionX += velocityX;
        positionY += velocityY;
    }

    public void draw(Canvas canvas, Paint paint) {
        //draw a circle at x, y
        canvas.drawCircle(positionX, positionY, radius, paint);
    }

    public float getPositionX() {
        return this.positionX;
    }

    public float getPositionY() {
        return this.positionY;
    }

    public void updateBallXPositionFromDatabase(float newPositionX){
        this.positionX = newPositionX;
    }
    public void updateBallYPositionFromDatabase(float newPositionY){
        this.positionY = newPositionY;
    }

    public void resetBall () {
        positionX = GameView.screenWidth / 2;
        positionY = GameView.screenHeight / 2;

        velocityX = (float) (Math.random() * 60) - 30;
        velocityY = Math.random() > 0.5 ?  17 : -17;

        if( velocityY > 0) {
            bounceDirection = true;
        }else{
            bounceDirection = false;
        }
    }
}
