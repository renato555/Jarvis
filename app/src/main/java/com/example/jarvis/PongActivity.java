package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String roomName;

        // TODO: 05/08/2020 na temelju moda pokrenuti drugacije konfiguracije ponga. ( HOST-1, JOIN-2, 2 PLAYERS-3)  
        Intent intent = getIntent();

        roomName = intent.getStringExtra("RoomName");

        setContentView( new GameView( this, roomName));
    }
}