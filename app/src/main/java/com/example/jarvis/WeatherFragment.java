package com.example.jarvis;


import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.drawable.Drawable;


import android.os.AsyncTask;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeatherFragment extends Fragment {

    private static final String[] times = {"5:00", "8:00", "11:00", "14:00", "17:00", "20:00"};
    private static Map<Drawable, String> imagesMap;
    private static Map<String, String> placesMap;
    private View view;
    private Spinner dropdownSpinner;

    private LinearLayout[] todayLayouts = new LinearLayout[6];

    private TextView todayDay;
    private TextView[] todayTimeViews = new TextView[6];
    private ImageView[] todayImageViews = new ImageView[6];
    private TextView[] todayDescriptionViews = new TextView[6];

    private TextView tomorrowDay;
    private TextView[] tomorrowTimeViews = new TextView[6];
    private ImageView[] tomorrowImageViews = new ImageView[6];
    private TextView[] tomorrowDescriptionViews = new TextView[6];

    private TextView dayAfterTomorrowDay;
    private TextView[] dayAfterTomorrowTimeViews = new TextView[6];
    private ImageView[] dayAfterTomorrowImageViews = new ImageView[6];
    private TextView[] dayAfterTomorrowDescriptionViews = new TextView[6];


    private OnSwipeTouchListener swipeListener;

    public WeatherFragment( OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        imagesMap = new WeatherMapLoader(getContext()).loadMap();

        placesMap = new PlacesLoader().loadPlaces(getContext());

        loadTodayLayouts(view);

        todayDay = (TextView) view.findViewById(R.id.todayDay);
        loadTodayTimeViews(view);
        loadTodayImageViews(view);
        loadTodayDescriptionViews(view);

        tomorrowDay = (TextView) view.findViewById(R.id.tomorrowDay);
        loadTomorrowTimeViews(view);
        loadTomorrowImageViews(view);
        loadTomorrowDescriptionViews(view);

        dayAfterTomorrowDay = (TextView) view.findViewById(R.id.dayAfterTomorrowDay);
        loadDayAfterTomorrowTimeViews(view);
        loadDayAfterTomorrowImageViews(view);
        loadDayAfterTomorrowDescriptionViews(view);

        dropdownSpinner = (Spinner) view.findViewById(R.id.weatherSpinner);
        setUpDropdownWeatherSpinner();

        return view;
    }

    private void loadWeather(String item) {
        new MyAsyncTask(item, todayDay, todayTimeViews, todayImageViews, todayDescriptionViews, 0).execute();
        new MyAsyncTask(item, tomorrowDay, tomorrowTimeViews, tomorrowImageViews, tomorrowDescriptionViews, 1).execute();
        new MyAsyncTask(item, dayAfterTomorrowDay, dayAfterTomorrowTimeViews, dayAfterTomorrowImageViews, dayAfterTomorrowDescriptionViews, 2).execute();
    }

    private void setUpDropdownWeatherSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(placesMap.keySet()));
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        dropdownSpinner.setAdapter(adapter);

        int i;
        if((i = getSavedDropdownWeatherSpinnerChoice()) != -1)
            dropdownSpinner.setSelection(i);

        dropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadWeather(adapter.getItem(i));
                saveDropdownWeatherSpinnerChoice(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void saveDropdownWeatherSpinnerChoice(int i) {
        try{
            FileOutputStream fos = getContext().openFileOutput(Constants.WEATHER_PLACE_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            os.writeObject(i);
            os.close();
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private int getSavedDropdownWeatherSpinnerChoice(){
        int returnPosition = -1;
        try(FileInputStream fis = getContext().openFileInput(Constants.WEATHER_PLACE_FILE);
            ObjectInputStream oi = new ObjectInputStream(fis);){

            returnPosition = (int) oi.readObject();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

        return returnPosition;
    }

    private void loadTodayLayouts(View view) {
        todayLayouts[0] = (LinearLayout) view.findViewById(R.id.todayLayout_5);
        todayLayouts[1] = (LinearLayout) view.findViewById(R.id.todayLayout_8);
        todayLayouts[2] = (LinearLayout) view.findViewById(R.id.todayLayout_11);
        todayLayouts[3] = (LinearLayout) view.findViewById(R.id.todayLayout_14);
        todayLayouts[4] = (LinearLayout) view.findViewById(R.id.todayLayout_17);
        todayLayouts[5] = (LinearLayout) view.findViewById(R.id.todayLayout_20);
    }

    private void loadTodayTimeViews(View view) {
        todayTimeViews[0] = (TextView) view.findViewById(R.id.todayTime_5);
        todayTimeViews[1] = (TextView) view.findViewById(R.id.todayTime_8);
        todayTimeViews[2] = (TextView) view.findViewById(R.id.todayTime_11);
        todayTimeViews[3] = (TextView) view.findViewById(R.id.todayTime_14);
        todayTimeViews[4] = (TextView) view.findViewById(R.id.todayTime_17);
        todayTimeViews[5] = (TextView) view.findViewById(R.id.todayTime_20);
    }

    private void loadTodayImageViews(View view){
        todayImageViews[0] = (ImageView) view.findViewById(R.id.todayImage_5);
        todayImageViews[1] = (ImageView) view.findViewById(R.id.todayImage_8);
        todayImageViews[2] = (ImageView) view.findViewById(R.id.todayImage_11);
        todayImageViews[3] = (ImageView) view.findViewById(R.id.todayImage_14);
        todayImageViews[4] = (ImageView) view.findViewById(R.id.todayImage_17);
        todayImageViews[5] = (ImageView) view.findViewById(R.id.todayImage_20);
    }

    private void loadTodayDescriptionViews(View view){
        todayDescriptionViews[0] = (TextView) view.findViewById(R.id.todayDescription_5);
        todayDescriptionViews[1] = (TextView) view.findViewById(R.id.todayDescription_8);
        todayDescriptionViews[2] = (TextView) view.findViewById(R.id.todayDescription_11);
        todayDescriptionViews[3] = (TextView) view.findViewById(R.id.todayDescription_14);
        todayDescriptionViews[4] = (TextView) view.findViewById(R.id.todayDescription_17);
        todayDescriptionViews[5] = (TextView) view.findViewById(R.id.todayDescription_20);
    }

    private void loadTomorrowTimeViews(View view) {
        tomorrowTimeViews[0] = (TextView) view.findViewById(R.id.tomorrowTime_5);
        tomorrowTimeViews[1] = (TextView) view.findViewById(R.id.tomorrowTime_8);
        tomorrowTimeViews[2] = (TextView) view.findViewById(R.id.tomorrowTime_11);
        tomorrowTimeViews[3] = (TextView) view.findViewById(R.id.tomorrowTime_14);
        tomorrowTimeViews[4] = (TextView) view.findViewById(R.id.tomorrowTime_17);
        tomorrowTimeViews[5] = (TextView) view.findViewById(R.id.tomorrowTime_20);
    }

    private void loadTomorrowImageViews(View view){
        tomorrowImageViews[0] = (ImageView) view.findViewById(R.id.tomorrowImage_5);
        tomorrowImageViews[1] = (ImageView) view.findViewById(R.id.tomorrowImage_8);
        tomorrowImageViews[2] = (ImageView) view.findViewById(R.id.tomorrowImage_11);
        tomorrowImageViews[3] = (ImageView) view.findViewById(R.id.tomorrowImage_14);
        tomorrowImageViews[4] = (ImageView) view.findViewById(R.id.tomorrowImage_17);
        tomorrowImageViews[5] = (ImageView) view.findViewById(R.id.tomorrowImage_20);
    }

    private void loadTomorrowDescriptionViews(View view){
        tomorrowDescriptionViews[0] = (TextView) view.findViewById(R.id.tomorrowDescription_5);
        tomorrowDescriptionViews[1] = (TextView) view.findViewById(R.id.tomorrowDescription_8);
        tomorrowDescriptionViews[2] = (TextView) view.findViewById(R.id.tomorrowDescription_11);
        tomorrowDescriptionViews[3] = (TextView) view.findViewById(R.id.tomorrowDescription_14);
        tomorrowDescriptionViews[4] = (TextView) view.findViewById(R.id.tomorrowDescription_17);
        tomorrowDescriptionViews[5] = (TextView) view.findViewById(R.id.tomorrowDescription_20);
    }

    private void loadDayAfterTomorrowTimeViews(View view) {
        dayAfterTomorrowTimeViews[0] = (TextView) view.findViewById(R.id.dayAfterTomorrowTime_5);
        dayAfterTomorrowTimeViews[1] = (TextView) view.findViewById(R.id.dayAfterTomorrowTime_8);
        dayAfterTomorrowTimeViews[2] = (TextView) view.findViewById(R.id.dayAfterTomorrowTime_11);
        dayAfterTomorrowTimeViews[3] = (TextView) view.findViewById(R.id.dayAfterTomorrowTime_14);
        dayAfterTomorrowTimeViews[4] = (TextView) view.findViewById(R.id.dayAfterTomorrowTime_17);
        dayAfterTomorrowTimeViews[5] = (TextView) view.findViewById(R.id.dayAfterTomorrowTime_20);
    }

    private void loadDayAfterTomorrowImageViews(View view){
        dayAfterTomorrowImageViews[0] = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage_5);
        dayAfterTomorrowImageViews[1] = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage_8);
        dayAfterTomorrowImageViews[2] = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage_11);
        dayAfterTomorrowImageViews[3] = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage_14);
        dayAfterTomorrowImageViews[4] = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage_17);
        dayAfterTomorrowImageViews[5] = (ImageView) view.findViewById(R.id.dayAfterTomorrowImage_20);
    }

    private void loadDayAfterTomorrowDescriptionViews(View view){
        dayAfterTomorrowDescriptionViews[0] = (TextView) view.findViewById(R.id.dayAfterTomorrowDescription_5);
        dayAfterTomorrowDescriptionViews[1] = (TextView) view.findViewById(R.id.dayAfterTomorrowDescription_8);
        dayAfterTomorrowDescriptionViews[2] = (TextView) view.findViewById(R.id.dayAfterTomorrowDescription_11);
        dayAfterTomorrowDescriptionViews[3] = (TextView) view.findViewById(R.id.dayAfterTomorrowDescription_14);
        dayAfterTomorrowDescriptionViews[4] = (TextView) view.findViewById(R.id.dayAfterTomorrowDescription_17);
        dayAfterTomorrowDescriptionViews[5] = (TextView) view.findViewById(R.id.dayAfterTomorrowDescription_20);
    }


    @SuppressLint("StaticFieldLeak")
    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private int start;
        private String tempDay;
        private String link;
        private String[] descriptions = new String[6];
        private Drawable[] images = new Drawable[6];
        private TextView day;
        private TextView[] timeViews;
        private ImageView[] imageViews;
        private TextView[] descriptionViews;
        private String[] temperatures = new String[6];

        public MyAsyncTask(String placeSelected, TextView day, TextView[] timeViews, ImageView[] imageViews, TextView[] descriptionViews, int start){
            link = placesMap.get(placeSelected);
            this.day = day;
            this.timeViews = timeViews;
            this.imageViews = imageViews;
            this.descriptionViews = descriptionViews;
            this.start = start;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(link).get();

                Elements table = doc.getElementsByClass("fd-c-table table-weather-7day");
                Elements tableElements = table.select("tr");

                tempDay = tableElements.get(start*3 + 1).text().split(" ")[0];


                for(int i = 0; i < 6; i++){

                    descriptions[i] = tableElements.get(start*3 + 1).select("td").get(i + 1).select("span").attr("title");

                    for (Map.Entry<Drawable, String> entry : imagesMap.entrySet()) {
                        if(descriptions[i].equals(entry.getValue())) {
                            images[i] = entry.getKey();
                            break;
                        }
                    }

                    temperatures[i] = tableElements.get(start*3 + 2).select("td").get(i + 1).text();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            day.setText(tempDay);
            for(int i = 0; i < 6; i++){
                if(temperatures[i].equals("")) {
                    todayLayouts[i].removeAllViews();
                }else {
                    timeViews[i].setText(times[i]);
                    imageViews[i].setImageDrawable(images[i]);
                    descriptionViews[i].setText(temperatures[i]);
                }
            }
        }
    }
}
