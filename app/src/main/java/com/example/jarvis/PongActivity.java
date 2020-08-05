package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( new GameView( this));
    }
}