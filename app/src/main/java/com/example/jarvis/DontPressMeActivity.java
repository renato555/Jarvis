package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DontPressMeActivity extends AppCompatActivity {

    private MaterialButton b176;
    private MaterialButton b177;
    private String link176;
    private String link177;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dont_press_me);

        b176 = (MaterialButton) findViewById(R.id.b176);
        b177 = (MaterialButton) findViewById(R.id.b177);


        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Document doc = Jsoup.connect(Constants.DONT_PRESS_ME_LINK).get();

                    link176 = doc.select("a[data-fileid='4683']").attr("href");
                    link176 = link176.replaceAll("\\s", "%20");

                    link177 = doc.select("a[data-fileid='4684']").attr("href");
                    link177 = link177.replaceAll("\\s", "%20");

                    b176.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToUrl(link176);
                        }
                    });

                    b177.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToUrl(link177);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }.execute();
        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        while(link177 == null){}
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

}