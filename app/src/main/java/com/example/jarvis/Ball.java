package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    private float positionX;
    private float positionY;
    
    private float velocityX;
    private float velocityY;

    private float radius = 50;

    public Ball(){
        //center the ball in the screen
        positionX = GameView.screenWidth / 2;
        positionY = GameView.screenHeight / 2;

        pickVelocity();
    }

    public void update( Player player1, Player player2, Score score){
        positionX += velocityX;
        positionY += velocityY;
        // TODO: 05/08/2020 check if it should bounce ( change velocity) and increase score if off grid 
        
    }
    
    public void draw(Canvas canvas, Paint paint){
        //draw a circle at x, y
        canvas.drawCircle( positionX, positionY, radius, paint);
    }

    private void pickVelocity(){
        velocityX = (float) (Math.random() * 10);
        velocityY = (float) (Math.random() * 10);
    }
    
}
