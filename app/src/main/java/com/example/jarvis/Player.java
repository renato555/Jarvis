package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Player {
    private float positionX;
    private float positionY;

    private double wantX;

    private float width = 700.f;
    private float height = 100.f;

    public Player( float startingX, float startingY){
        positionX = startingX;
        positionY = startingY;
    }

    public void update(){
        // TODO: 05/08/2020 for now the player will just follow the finger
        positionX += 0.2*( wantX - positionX);
    }
    
    public void draw( Canvas canvas, Paint paint){
        //draw a rectange at x, y with width width
        canvas.drawRect( positionX, positionY, width, height, paint);
    }

    public void wantedPosition( MotionEvent event){
        //set wanted position where the finger is pointed
        //positionX = event.getX();
    }
    
}
