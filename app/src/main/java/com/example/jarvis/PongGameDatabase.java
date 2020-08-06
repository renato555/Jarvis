package com.example.jarvis;

import android.util.Pair;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PongGameDatabase {

    private FirebaseDatabase database;
    private DatabaseReference player1Ref;
    private DatabaseReference player2Ref;
    private DatabaseReference ballRef;

    public PongGameDatabase(String roomName){
        database = FirebaseDatabase.getInstance();

        //setup reference paths
        player1Ref = database.getReference("rooms/" + roomName + "/player1");
        player2Ref = database.getReference("rooms/" + roomName + "/player2");
        ballRef = database.getReference("rooms/" + roomName + "/ball");
    }

    public void update(double p1px, double p2px, double bpx, double bpy){
        player1Ref.setValue(p1px);
        player2Ref.setValue(p2px);
        //ballRef.setValue(new Pair<>(bpx, bpy));
    }
}
