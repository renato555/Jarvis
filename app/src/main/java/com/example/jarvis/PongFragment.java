package com.example.jarvis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PongFragment extends Fragment {

    private OnSwipeTouchListener swipeListener;

    private String playerName = "";
    private String roomName = "";

    private FirebaseDatabase database;
    private DatabaseReference playerRef;
    private DatabaseReference roomRef;

    private Button host;
    private Button join;
    private Button players2;

    public PongFragment(OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pong, container, false);

        database = FirebaseDatabase.getInstance();

        loginAsPlayer();

        //load views
        loadViews( view);

        //setUp button listeners
        setUpListeners();
        return view;
    }

    private void loginAsPlayer() {

        playerName = ConnectionWithWebsite.getUserFullName().split(" ")[0];
        System.out.println(playerName);
        if(!playerName.equals("") && !playerName.equals("Error")){
            playerRef = database.getReference("players/" + playerName);
            addEventListener();
            playerRef.setValue("");
        }

        //check if the player exists and reference
//        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", 0);
//        playerName = preferences.getString("playerName", "");
        if(!playerName.equals("") && !playerName.equals("Error")){
            playerRef = database.getReference("players/" + playerName);
            addEventListener();
            playerRef.setValue("");
        }
    }

    private void addEventListener() {
        //read from database
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!playerName.equals("") && !playerName.equals("Error")){
                    SharedPreferences preferences = getContext().getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadViews( View view){
        host = ( Button) view.findViewById( R.id.buttonHost);
        join = ( Button) view.findViewById( R.id.buttonJoin);
        players2 = ( Button) view.findViewById( R.id.button2Players);
    }

    private void setUpListeners(){
        host.setOnClickListener( (View v) -> {
            roomName = playerName;
            roomRef = database.getReference("rooms/" + roomName + "/player1");
            roomRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //join the room
                    Intent intent = new Intent(getContext(), PongActivity.class);
                    intent.putExtra("RoomName", roomName);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        join.setOnClickListener( (View v) -> {
            startActivity(new Intent(getContext(), PongRoomActivity.class));
        });

        players2.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getContext(), PongActivity.class);
            intent.putExtra( "mode", 3);
            startActivity( intent);
        });
    }
}
