package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

public class Player {
    private float positionX;
    private float positionY;

    private double wantX;

    public static final float width = 400.f;
    public static final float height = 70.f;

    public Player( float startingX, float startingY){
        positionX = startingX;
        positionY = startingY;

        wantX = startingX;
    }

    public void update(){
        // TODO: 05/08/2020 for now the player will just follow the finger
        positionX += 0.06*( wantX - positionX);
    }
    
    public void draw( Canvas canvas, Paint paint){
        //draw a rectange at x, y ( x is in the midle)
        canvas.drawRect( positionX - (width /2), positionY, positionX + (width / 2), positionY - height, paint);
    }

    public void wantedPosition( float wantX){
        //set wanted position where the finger is pointed
        this.wantX = wantX;
    }
}
