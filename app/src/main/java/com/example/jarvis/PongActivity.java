package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PongActivity extends AppCompatActivity {

    private boolean isHost;
    private String roomName;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: 05/08/2020 na temelju moda pokrenuti drugacije konfiguracije ponga. ( HOST-1, JOIN-2, 2 PLAYERS-3)  
        Intent intent = getIntent();

        roomName = intent.getStringExtra("RoomName");
        if(ConnectionWithWebsite.getUserFullName().split(" ")[0].equals(roomName))
            isHost = true;
        else isHost = false;

        mode = intent.getIntExtra("mode", 0);

        setContentView( new GameView( this, roomName, isHost, mode));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Delete room when no players remain in it
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms/" + roomName);
        roomRef.removeValue();

    }
}