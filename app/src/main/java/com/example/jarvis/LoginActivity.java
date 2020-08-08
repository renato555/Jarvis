package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (MaterialButton) findViewById(R.id.loginBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility( View.GONE);

        BroadcastReceiver onComplete = (new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if( intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE) startMain();
            }
        });
        registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        readAccount();
        progressBar.setVisibility( View.VISIBLE);
        loginButton.setEnabled( false);
        new Thread(){
            private Handler mainHandler = new Handler( LoginActivity.this.getMainLooper());
            @Override
            public void run() {
                if( !username.isEmpty() && !password.isEmpty() && ConnectionWithWebsite.tryLogin( LoginActivity.this, username, password)) {
                    login();
                }else{
                    mainHandler.post( () -> {
                        progressBar.setVisibility( View.GONE);
                        loginButton.setEnabled( true);
                    });
                }
            }
        }.start();


        loginButton.setOnClickListener(view -> {
            progressBar.setVisibility( View.VISIBLE);
            loginButton.setEnabled( false);
            closeKeyboard();
            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();

            new Thread(){
                private Handler mainHandler = new Handler( LoginActivity.this.getMainLooper());
                @Override
                public void run() {
                    if( ConnectionWithWebsite.tryLogin( LoginActivity.this, username, password)){
                        mainHandler.post( () ->{
                            saveAccount( getApplicationContext(), username, password);
                            login();
                        });
                    }else{
                        mainHandler.post( () -> {
                            progressBar.setVisibility( View.GONE);
                            loginButton.setEnabled( true);
                            Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }.start();
        });
    }

    private void readAccount() {
        try{
            FileInputStream fis = getApplicationContext().openFileInput( Constants.USERNAME_AND_PASSWORD_FILE);
            ObjectInputStream oi = new ObjectInputStream( fis);
            String temp =(String) oi.readObject();
            if( temp.length() > 1){
                String[] split = temp.split("/");
                username = split[0];
                password = split[1];
            }
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

    public static void saveAccount( Context context, String username, String password){
        try{
            FileOutputStream fos = context.openFileOutput(Constants.USERNAME_AND_PASSWORD_FILE, Context.MODE_PRIVATE);
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
    
    //returns true if downloading has started and false otherwise
    private boolean updateFilesIfNeeded(){
        return ConnectionWithWebsite.calendarPeriodicDownload( getApplicationContext());
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if( view != null){
            InputMethodManager manager = ( InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(), 0);
        }
    }

    private void login(){
        if( !updateFilesIfNeeded()){
            startMain();
        }
        //broadcast receiver will startMain when downloading has been completed
    }

    private void startMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        startActivity(intent);
        finish();
    }

    public static void logout(Context context, Activity currentActivity){
        LoginActivity.saveAccount( context, "", "");
        Intent intent = new Intent(context, LoginActivity.class);
        currentActivity.startActivity( intent);
        currentActivity.finish();
    }
}