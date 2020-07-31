package com.example.jarvis;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

public class WeatherMapLoader {

    private Context context;

    public WeatherMapLoader(Context current){
        context = current;
    }

    public Map<Drawable, String> loadMap(){

        Resources resources = context.getResources();
        Map<Drawable, String> map = new HashMap<>();

        map.put(resources.getDrawable(R.drawable.ic_1), Constants.IMAGE_1);
        map.put(resources.getDrawable(R.drawable.ic_2), Constants.IMAGE_2);
        map.put(resources.getDrawable(R.drawable.ic_3), Constants.IMAGE_3);
        map.put(resources.getDrawable(R.drawable.ic_4), Constants.IMAGE_4);
        map.put(resources.getDrawable(R.drawable.ic_5), Constants.IMAGE_5);
        map.put(resources.getDrawable(R.drawable.ic_6), Constants.IMAGE_6);
        map.put(resources.getDrawable(R.drawable.ic_7), Constants.IMAGE_7);
        map.put(resources.getDrawable(R.drawable.ic_8), Constants.IMAGE_8);
        map.put(resources.getDrawable(R.drawable.ic_9), Constants.IMAGE_9);
        map.put(resources.getDrawable(R.drawable.ic_10), Constants.IMAGE_10);
        map.put(resources.getDrawable(R.drawable.ic_11), Constants.IMAGE_11);
        map.put(resources.getDrawable(R.drawable.ic_12), Constants.IMAGE_12);
        map.put(resources.getDrawable(R.drawable.ic_13), Constants.IMAGE_13);
        map.put(resources.getDrawable(R.drawable.ic_14), Constants.IMAGE_14);
        map.put(resources.getDrawable(R.drawable.ic_15), Constants.IMAGE_15);
        map.put(resources.getDrawable(R.drawable.ic_16), Constants.IMAGE_16);
        map.put(resources.getDrawable(R.drawable.ic_17), Constants.IMAGE_17);
        map.put(resources.getDrawable(R.drawable.ic_18), Constants.IMAGE_18);
        map.put(resources.getDrawable(R.drawable.ic_19), Constants.IMAGE_19);
        map.put(resources.getDrawable(R.drawable.ic_20), Constants.IMAGE_20);
        map.put(resources.getDrawable(R.drawable.ic_21), Constants.IMAGE_21);
        map.put(resources.getDrawable(R.drawable.ic_22), Constants.IMAGE_22);
        map.put(resources.getDrawable(R.drawable.ic_23), Constants.IMAGE_23);
        map.put(resources.getDrawable(R.drawable.ic_24), Constants.IMAGE_24);
        map.put(resources.getDrawable(R.drawable.ic_25), Constants.IMAGE_25);
        map.put(resources.getDrawable(R.drawable.ic_26), Constants.IMAGE_26);
        map.put(resources.getDrawable(R.drawable.ic_27), Constants.IMAGE_27);
        map.put(resources.getDrawable(R.drawable.ic_28), Constants.IMAGE_28);
        map.put(resources.getDrawable(R.drawable.ic_29), Constants.IMAGE_29);
        map.put(resources.getDrawable(R.drawable.ic_30), Constants.IMAGE_30);
        map.put(resources.getDrawable(R.drawable.ic_31), Constants.IMAGE_31);
        map.put(resources.getDrawable(R.drawable.ic_32), Constants.IMAGE_32);
        map.put(resources.getDrawable(R.drawable.ic_33), Constants.IMAGE_33);
        map.put(resources.getDrawable(R.drawable.ic_34), Constants.IMAGE_34);
        map.put(resources.getDrawable(R.drawable.ic_35), Constants.IMAGE_35);
        map.put(resources.getDrawable(R.drawable.ic_36), Constants.IMAGE_36);
        map.put(resources.getDrawable(R.drawable.ic_37), Constants.IMAGE_37);
        map.put(resources.getDrawable(R.drawable.ic_38), Constants.IMAGE_38);
        map.put(resources.getDrawable(R.drawable.ic_39), Constants.IMAGE_39);
        map.put(resources.getDrawable(R.drawable.ic_40), Constants.IMAGE_40);
        map.put(resources.getDrawable(R.drawable.ic_41), Constants.IMAGE_41);
        map.put(resources.getDrawable(R.drawable.ic_42), Constants.IMAGE_42);

        return map;
    }

}
