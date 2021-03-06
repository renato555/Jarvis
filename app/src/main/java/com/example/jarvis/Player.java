package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Player {
    private String playerName;

    private float positionX;
    private float positionY;

    private double wantX;

    public static final float width = (float) 0.25 * GameView.screenWidth;
    public static final float height = (float) 0.03 * GameView.screenHeight;
    public static final float imaginaryHeight = 2 * height;
    public static final float playerOffSet =(float) 0.1 * GameView.screenHeight;

    public Player( float startingX, float startingY, String playerName){
        positionX = startingX;
        positionY = startingY;
        this.playerName = playerName;

        wantX = startingX;
    }

    public void update(){
        //player will just follow the finger
        positionX += 0.1*( wantX - positionX);
    }
    
    public void draw( Canvas canvas, Paint paint){
        //draw a rectangle at x, y ( x is in the middle)
        canvas.drawRect( positionX - (width /2), positionY, positionX + (width / 2), positionY + height, paint);
    }

    public void wantedPosition( float wantX){
        //set wanted position where the finger is pointed
        this.wantX = wantX;
    }

    public float getPositionX(){ return positionX;}
    public float getPositionY(){ return positionY;}

    public void updatePositionFromDatabase(float newPositionX){
        this.positionX =(float) newPositionX;
        this.wantX = (float) newPositionX;
    }
    public String getPlayerName(){ return playerName;}
}
