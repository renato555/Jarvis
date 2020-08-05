package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PongGameActivity extends AppCompatActivity {

    private Button pokeButton;

    private String playerName = "";
    private String roomName = "";
    private String role = "";
    private String message = "";

    private FirebaseDatabase database;
    private DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong_game);

        pokeButton = (Button) findViewById(R.id.pokeButton);
        pokeButton.setEnabled(false);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("RoomName");
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "quest";
            }
        }

        pokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send message
                pokeButton.setEnabled(false);
                message = role + ": Poked";
                messageRef.setValue(message);

            }
        });

        messageRef = database.getReference("rooms/" + roomName + "/message");
        message = role + ": Poked";
        messageRef.setValue(message);
        addRoomEventListener();
    }

    private void addRoomEventListener(){
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(role.equals("host")){
                    if(snapshot.getValue(String.class).contains("quest:")){
                        pokeButton.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "" + snapshot.getValue(String.class).replace("quest:", ""), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(snapshot.getValue(String.class).contains("host:")){
                        pokeButton.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error - retry
                messageRef.setValue(message);
            }
        });
    }
}