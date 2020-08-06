package com.example.jarvis;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Player {
    private float positionX;
    private float positionY;

    private double wantX;

    private FirebaseDatabase database;
    private DatabaseReference playerRef;

    private boolean isPlayer1; // if true -> player 1; if false -> player 2
    private String roomName = "";

    public static final float width = 300.f;
    public static final float height = 40.f;
    public static final float imaginaryHeight = 80;
    public static final float playerOffSet = 600;

    public Player( float startingX, float startingY, boolean isPlayer1, String roomName){
        positionX = startingX;
        positionY = startingY;

        wantX = startingX;

        this.isPlayer1 = isPlayer1;
        this.roomName = roomName;

        //setupDatabase();
    }

    public void update(){
        // TODO: 05/08/2020 for now the player will just follow the finger
        positionX += 0.06*( wantX - positionX);
        //player will just follow the finger
        positionX += 0.1*( wantX - positionX);
    }
    
    public void draw( Canvas canvas, Paint paint){
        //draw a rectange at x, y ( x is in the midle)
        canvas.drawRect( positionX - (width /2), positionY, positionX + (width / 2), positionY + height, paint);
    }

    public void wantedPosition( float wantX){
        //set wanted position where the finger is pointed
        this.wantX = wantX;
    }


    public void updateDatabase(){
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerRef.setValue(positionX);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //do nothing
            }
        });
    }

    public float getPositionX(){ return positionX;}
    public float getPositionY(){ return positionY;}
}
