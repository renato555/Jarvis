package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        readAccount();
//        if(true) // TU metoda za provjeru usernamea i passworda
//            startMain();

        setContentView(R.layout.activity_login);


        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (MaterialButton) findViewById(R.id.loginBtn);

        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        loginButton.setOnClickListener(view -> {
            if(true){ // Tu metoda za provjeru usenamea i passworda
                saveAccount();
                startMain();
            }else
                Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT);
        });
    }

    private void readAccount() {
        try{
            FileInputStream fis = getApplicationContext().openFileInput( Constants.TODO_DATABASE_FILE);
            ObjectInputStream oi = new ObjectInputStream( fis);
            String temp = oi.readLine();
            String[] split = temp.split("/");
            username = split[0];
            password = split[1];
            fis.close();
            oi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveAccount(){
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput( Constants.USERNAME_AND_PASSWORD_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream( fos);
            o.writeChars(username + "/" + password);
            o.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }
}