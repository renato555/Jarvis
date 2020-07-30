package com.example.jarvis;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class WeatherFragment extends Fragment {

    private TextView todayDescription;
    private ImageView todayImage;
    private TextView todayText;
    private TextView tomorrowDescription;
    private ImageView tomorrowImage;
    private TextView tomorrowText;
    private TextView dayAfterTomorrowDescription;
    private ImageView dayAfterTomorrowImage;
    private TextView dayAfterTomorrowText;
    private Button reloadButton;

    private OnSwipeTouchListener swipeListener;

    public WeatherFragment( OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        reloadButton = (Button) view.findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeather();
            }
        });

        loadViews(view);
        loadWeather();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
    }

    public void loadViews(View view) {
        todayText = (TextView) view.findViewById(R.id.today);
        todayDescription = (TextView) view.findViewById(R.id.todayText);
        todayImage = (ImageView) view.findViewById(R.id.todayImage);
        tomorrowText = (TextView) view.findViewById(R.id.tomorrow);
        tomorrowDescription = (TextView) view.findViewById(R.id.tomorrowText);
        tomorrowImage = (ImageView) view.findViewById(R.id.tomorrowImage);
        dayAfterTomorrowText = (TextView) view.findViewById(R.id.dayAfterTomorrow);
        dayAfterTomorrowDescription = (TextView) view.findViewById(R.id.dayAFterTomorrowText);
        dayAfterTomorrowImage = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage);
    }

    public void loadWeather(){
        startLoading(getView());
    }

    public void startLoading(View view) {
        new MyAsyncTask(todayText, todayDescription, todayImage, Constants.TODAY_IMAGE_CLASS_STRING, 0).execute();
        new MyAsyncTask(tomorrowText, tomorrowDescription, tomorrowImage, Constants.OTHER_DAY_IMAGE_CLASS_STRING, 1).execute();
        new MyAsyncTask(dayAfterTomorrowText, dayAfterTomorrowDescription, dayAfterTomorrowImage, Constants.OTHER_DAY_IMAGE_CLASS_STRING, 2).execute();
    }

    @SuppressLint("StaticFieldLeak")
    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ImageView imageView;
        private TextView textView;
        private TextView dayText;
        private String description;
        private String imageLink;
        private int dayNumber;
        private String imageClass;
        private String day;

        public MyAsyncTask(TextView dayText, TextView textView, ImageView imageView, String imageClass, int dayNumber) {
            super();
            this.dayText = dayText;
            this.textView = textView;
            this.imageClass = imageClass;
            this.dayNumber = dayNumber;
            this.imageView = imageView;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(Constants.WEATHER_LINK).get();
                Element imageElement;
                Elements elementsByImageClass = doc.getElementsByClass(imageClass);

                if (imageClass.equals(Constants.TODAY_IMAGE_CLASS_STRING))
                    imageElement = elementsByImageClass.first();
                else
                    imageElement = elementsByImageClass.get(dayNumber);

                day = doc.getElementsByClass(Constants.DAY_CLASS_STRING).not(Constants.EXCLUDED_DAY_CLASS).get(dayNumber * 2).attr("aria-label");
                imageLink = imageElement.select("img").first().absUrl("src");

                Element descriptionElement1 = doc.getElementsByClass(Constants.TEMPERATURE_CLASS_STRING).not(Constants.EXCLUDED_TEMPERATURE_CLASS).get(dayNumber + 1).select("span").first();
                Element descriptionElement2 = imageElement.select("img").first();
                description = descriptionElement1.text() + "°C\n" + descriptionElement2.attr("alt");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new DownloadImageTask(imageView).execute(imageLink);
            textView.setText(description);
            dayText.setText((day));
        }
    }


}
