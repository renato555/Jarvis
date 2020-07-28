package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        readAccount();
        if( ConnectionWithWebsite.tryLogin( username, password)) {
            startMain();
        }

        setContentView(R.layout.activity_login);


        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (MaterialButton) findViewById(R.id.loginBtn);
        
        loginButton.setOnClickListener(view -> {
            closeKeyboard();
            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();

            if( ConnectionWithWebsite.tryLogin( username, password)){ // Tu metoda za provjeru usenamea i passworda
                saveAccount();
                startMain();
            }else
                Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
        });
    }

    private void readAccount() {
        try{

            FileInputStream fis = getApplicationContext().openFileInput( Constants.USERNAME_AND_PASSWORD_FILE);
            ObjectInputStream oi = new ObjectInputStream( fis);
            String temp =(String) oi.readObject();
            String[] split = temp.split("/");
            username = split[0];
            password = split[1];
            fis.close();
            oi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void saveAccount(){
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput(Constants.USERNAME_AND_PASSWORD_FILE, Context.MODE_APPEND);
            ObjectOutputStream o = new ObjectOutputStream( fos);
            o.writeObject(username + "/" + password);
            o.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if( view != null){
            InputMethodManager manager = ( InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(), 0);
        }
    }

    private void startMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        startActivity(intent);
        finish();
    }
}