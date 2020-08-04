package com.example.jarvis;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class PlacesLoader {

    public Map<String, String> loadPlaces(Context context){
        Map<String, String> returnMap = new HashMap<>();

        returnMap.put(context.getResources().getString(R.string.Zagreb), Constants.ZAGREB);
        returnMap.put(context.getResources().getString(R.string.Bistra), Constants.BISTRA);
        returnMap.put(context.getResources().getString(R.string.Tisno), Constants.TISNO);

        return returnMap;
    }
}
