package com.example.jarvis;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private MaterialButton reloadButton;

    private static Bundle savedState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        reloadButton = (MaterialButton) view.findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeather();
            }
        });

        loadViews(view);
        if (savedState != null) {
            restoreState();
        } else {
            loadWeather();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveState();
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
        pleaseWait();
        startLoading(getView());
    }

    public void saveState() {
        savedState = new Bundle();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap;

        //Check if it didn't finish loading
        if(((BitmapDrawable) dayAfterTomorrowImage.getDrawable()).getBitmap().equals(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_white_screen)).getBitmap())){
            savedState = null;
            return;
        }

        //Save Today State
        savedState.putString(Constants.TODAY_KEY, todayText.getText().toString());
        bitmap = ((BitmapDrawable) todayImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        savedState.putByteArray(Constants.TODAY_IMAGE_KEY, baos.toByteArray());
        savedState.putString(Constants.TODAY_DESCRIPTION_KEY, todayDescription.getText().toString());

        //Save Tomorrow State
        baos = new ByteArrayOutputStream();
        savedState.putString(Constants.TOMORROW_KEY, tomorrowText.getText().toString());
        bitmap = ((BitmapDrawable) tomorrowImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        savedState.putByteArray(Constants.TOMORROW_IMAGE_KEY, baos.toByteArray());
        savedState.putString(Constants.TOMORROW_DESCRIPTION_KEY, tomorrowDescription.getText().toString());

        //Save Day After Tomorrow state
        baos = new ByteArrayOutputStream();
        savedState.putString(Constants.DAY_AFTER_TOMORROW_KEY, dayAfterTomorrowText.getText().toString());
        bitmap = ((BitmapDrawable) dayAfterTomorrowImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        savedState.putByteArray(Constants.DAY_AFTER_TOMORROW_IMAGE_KEY, baos.toByteArray());
        savedState.putString(Constants.DAY_AFTER_TOMORROW_DESCRIPTION_KEY, dayAfterTomorrowDescription.getText().toString());
    }

    public void restoreState() {
        Bitmap bitmap;
        byte[] byteArray;

        //Restore Today State
        todayText.setText(savedState.get(Constants.TODAY_KEY).toString());
        byteArray = ((byte[]) savedState.get(Constants.TODAY_IMAGE_KEY));
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        todayImage.setImageBitmap(Bitmap.createBitmap(bitmap));
        todayDescription.setText(savedState.get(Constants.TODAY_DESCRIPTION_KEY).toString());

        // Restore Tomorrow state
        tomorrowText.setText(savedState.get(Constants.TOMORROW_KEY).toString());
        byteArray = ((byte[]) savedState.get(Constants.TOMORROW_IMAGE_KEY));
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        tomorrowImage.setImageBitmap(Bitmap.createBitmap(bitmap));
        tomorrowDescription.setText(savedState.get(Constants.TOMORROW_DESCRIPTION_KEY).toString());

        //Restore Day After Tomorrow state
        dayAfterTomorrowText.setText(savedState.get(Constants.DAY_AFTER_TOMORROW_KEY).toString());
        byteArray = ((byte[]) savedState.get(Constants.DAY_AFTER_TOMORROW_IMAGE_KEY));
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        dayAfterTomorrowImage.setImageBitmap(Bitmap.createBitmap(bitmap));
        dayAfterTomorrowDescription.setText(savedState.get(Constants.DAY_AFTER_TOMORROW_DESCRIPTION_KEY).toString());
    }

    public void pleaseWait(){
        int waitForLenght = 2;
        for(int i = 0; i < waitForLenght; i++)
            Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_LONG).show();
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
