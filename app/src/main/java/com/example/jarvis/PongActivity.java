package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Test", "Creating PongActivity");
        String roomName;
        boolean isHost;

        // TODO: 05/08/2020 na temelju moda pokrenuti drugacije konfiguracije ponga. ( HOST-1, JOIN-2, 2 PLAYERS-3)  
        Intent intent = getIntent();

        roomName = intent.getStringExtra("RoomName");
        if(ConnectionWithWebsite.getUserFullName().split(" ")[0].equals(roomName))
            isHost = true;
        else isHost = false;

        setContentView( new GameView( this, roomName, isHost));
    }
}