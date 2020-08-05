package com.example.jarvis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText playerNameEditText;
    private Button loginButton;

    private String playerName = "";

    private FirebaseDatabase database;
    private DatabaseReference playerRef;

    public PongFragment(OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    private Button host;
    private Button join;
    private Button players2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pong, container, false);

        playerNameEditText = (EditText) view.findViewById(R.id.playerName);
        loginButton = (Button) view.findViewById(R.id.playerLoginButton);

        database = FirebaseDatabase.getInstance();

        //check if the player exists and reference
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        if(!playerName.equals("")){
            playerRef = database.getReference("players/" + playerName);
            addEventListener();
            playerRef.setValue("");
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logging the player in
                playerName = playerNameEditText.getText().toString();
                playerNameEditText.setText("");
                if(!playerName.equals("")){
                    loginButton.setTag("Logging in");
                    loginButton.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener();
                    playerRef.setValue("");
                }
            }
        });
        loadViews( view);
        setUpListers();

        return view;
    }

    private void addEventListener() {
        //read from database
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!playerName.equals("")){
                    SharedPreferences preferences = getContext().getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();

                    startActivity(new Intent(getContext(), PongRoomActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginButton.setText(getResources().getString(R.string.login));
                loginButton.setEnabled(true);
                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadViews( View view){
        host = ( Button) view.findViewById( R.id.buttonHost);
        join = ( Button) view.findViewById( R.id.buttonJoin);
        players2 = ( Button) view.findViewById( R.id.button2Players);
    }

    private void setUpListers(){
        host.setOnClickListener( (View v) -> {

        });

        join.setOnClickListener( (View v) -> {

        });

        players2.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getContext(), PongActivity.class);
            intent.putExtra( "mode", 3);
            startActivity( intent);
        });
    }
}
