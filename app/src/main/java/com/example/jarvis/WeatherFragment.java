package com.example.jarvis;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.ByteArrayOutputStream;
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


    private final String WEATHER_LINK = "https://www.google.com/search?q=weather+forecast&rlz=1C1CHBD_enHR853HR853&oq=weather&aqs=chrome.0.69i59l2j69i57j0j69i60l4.2743j1j9&sourceid=chrome&ie=UTF-8";
    private final String TODAY_IMAGE_CLASS_STRING = "wob_df wob_ds";
    private final String OTHER_DAY_IMAGE_CLASS_STRING = "wob_df";
    private final String TEMPERATURE_CLASS_STRING = "vk_gy";
    private final String DAY_CLASS_STRING = "QrNVmd";
    private final String EXCLUDED_TEMPERATURE_CLASS = ".vk_sh";
    private final String EXCLUDED_DAY_CLASS = ".wob_hw";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        loadWeather(view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        System.out.println("nsita");

        if (savedInstanceState != null) {

            Bitmap bitmap;
            byte[] byteArray;

            byteArray = ((byte[]) savedInstanceState.get("TodayImageKey"));
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            todayImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, todayImage.getWidth(), todayImage.getHeight(), false));
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap;

        outState.putString("TodayKey", todayText.getText().toString());
        bitmap = ((BitmapDrawable) todayImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        outState.putByteArray("TodayImageKey", baos.toByteArray());
        outState.putString("TodayDescriptionKey", todayDescription.getText().toString());
    }

    public void loadWeather(View view) {
        todayText = (TextView) view.findViewById(R.id.today);
        todayDescription = (TextView) view.findViewById(R.id.todayText);
        todayImage = (ImageView) view.findViewById(R.id.todayImage);
        new MyAsyncTask(todayText, todayDescription, todayImage, TODAY_IMAGE_CLASS_STRING, 0).execute();

        tomorrowText = (TextView) view.findViewById(R.id.tomorrow);
        tomorrowDescription = (TextView) view.findViewById(R.id.tomorrowText);
        tomorrowImage = (ImageView) view.findViewById(R.id.tomorrowImage);
        new MyAsyncTask(tomorrowText, tomorrowDescription, tomorrowImage, OTHER_DAY_IMAGE_CLASS_STRING, 1).execute();

        dayAfterTomorrowText = (TextView) view.findViewById(R.id.dayAfterTomorrow);
        dayAfterTomorrowDescription = (TextView) view.findViewById(R.id.dayAFterTomorrowText);
        dayAfterTomorrowImage = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage);
        new MyAsyncTask(dayAfterTomorrowText, dayAfterTomorrowDescription, dayAfterTomorrowImage, OTHER_DAY_IMAGE_CLASS_STRING, 2).execute();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private String description;
        private String imageLink;
        private int dayNumber;
        private ImageView imageView;
        private TextView textView;
        private String imageClass;
        private TextView dayText;
        private String day;

        public MyAsyncTask(TextView dayText, TextView textView, ImageView imageView, String imageClass, int dayNumber) {
            this.dayText = dayText;
            this.textView = textView;
            this.imageClass = imageClass;
            this.dayNumber = dayNumber;
            this.imageView = imageView;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(WEATHER_LINK).get();
                Element imageElement;
                Elements elementsByImageClass = doc.getElementsByClass(imageClass);

                if (imageClass.equals(TODAY_IMAGE_CLASS_STRING))
                    imageElement = elementsByImageClass.first();
                else
                    imageElement = elementsByImageClass.get(dayNumber);

                day = doc.getElementsByClass(DAY_CLASS_STRING).not(EXCLUDED_DAY_CLASS).get(dayNumber * 2).attr("aria-label");
                imageLink = imageElement.select("img").first().absUrl("src");

                Element descriptionElement1 = doc.getElementsByClass(TEMPERATURE_CLASS_STRING).not(EXCLUDED_TEMPERATURE_CLASS).get(dayNumber + 1).select("span").first();
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
