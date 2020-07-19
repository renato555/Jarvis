package com.example.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;


public class WeatherFragment extends Fragment {

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        loadHTML(view);
        return view;
    }

    public void loadHTML(View view){
        try {

            textView = (TextView) view.findViewById(R.id.testView);
            Log.i("TEST", "Shit happens");

            Document doc = Jsoup.connect("http://example.com").get();



            Element content = doc.getElementById("primary");

            textView.setText(content.text());
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
