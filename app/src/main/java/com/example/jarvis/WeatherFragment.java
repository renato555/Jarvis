package com.example.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Document;

import java.io.IOException;


public class WeatherFragment extends Fragment {

    private EditText textView;
    private ImageView image;
    private final String TODAY_WEATHER_LINK = "https://meteo.hr/prognoze.php?section=prognoze_metp&param=zgdanas";
    private final String TOMORROW_WEATHER_LINK = "https://meteo.hr/prognoze.php?section=prognoze_metp&param=zgsutra";
    private String[] weatherLinks = {TODAY_WEATHER_LINK, TOMORROW_WEATHER_LINK};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        textView = (EditText) view.findViewById(R.id.weatherText);
        image = (ImageView) view.findViewById(R.id.imageView);
        new MyAsyncTask(0).execute();
        return view;
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, Void>{

        private View view;
        private String words;
        private String imageLink;
        private int dayNumber;

        public MyAsyncTask(int dayNumber) {
            this.dayNumber = dayNumber;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /*
            try {
                Document doc = Jsoup.connect(weatherLinks[dayNumber]).get();

                imageLink = doc.select("img").last().absUrl("src");

            }catch(IOException ex){
                ex.printStackTrace();
            }
            */
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new DownloadImageTask(image).execute(imageLink);
            //textView.setText(words);
        }
    }


}
