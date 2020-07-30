package com.example.jarvis;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


public class WeatherFragment extends Fragment {

    private static final String[] times = {"5:00", "8:00", "11:00", "14:00", "17:00", "20:00"};
    private static Map<Drawable, String> map;

    private TextView[] timeViews = new TextView[6];


    //private static Bundle savedState;

    private OnSwipeTouchListener swipeListener;

    public WeatherFragment( OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        map = new WeatherMapLoader(getContext()).loadMap();

        loadTimeViews(view);

        new MyAsyncTask().execute();
        return view;
    }

    private void loadTimeViews(View view) {
        timeViews[0] = (TextView) view.findViewById(R.id.todayTime_5);
        timeViews[1] = (TextView) view.findViewById(R.id.todayTime_8);
        timeViews[2] = (TextView) view.findViewById(R.id.todayTime_11);
        timeViews[3] = (TextView) view.findViewById(R.id.todayTime_14);
        timeViews[4] = (TextView) view.findViewById(R.id.todayTime_17);
        timeViews[5] = (TextView) view.findViewById(R.id.todayTime_20);
    }


    public void startLoading(View view) {
    }

    @SuppressLint("StaticFieldLeak")
    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private String day;
        private String[] descriptions = new String[6];
        private Drawable[] images = new Drawable[6];

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(Constants.WEATHER_LINK).get();

                Elements table = doc.getElementsByClass("fd-c-table table-weather-7day");
                Elements tableElements = table.select("tr");

                day = tableElements.get(1).text().split(" ")[0];

                Log.i("Test", tableElements.get(1).select("td").text());
                for(int i = 0; i < 6; i++){
                    descriptions[i] = tableElements.get(1).select("td").get(i + 2).select("span").attr("title");
                    Log.i("Test", descriptions[i]);
                }

                for(int i = 0; i < 6; i++) {
                    for (Map.Entry<Drawable, String> entry : map.entrySet()) {
                        if(descriptions[i].equals(entry.getValue())) {
                            images[i] = entry.getKey();
                            break;
                        }
                    }
                }


            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
