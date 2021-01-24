package com.example.covid19_reminder.yichen;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getSinglePlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlaces = "-NA-";
        String formatted_address = "-NA-";
        String latitude = "";
        String longtitude = "";

        try {
            if (!googlePlaceJSON.isNull("name")) {
                NameOfPlaces = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")) {
                formatted_address = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longtitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlaceMap.put("place_name", NameOfPlaces);
            googlePlaceMap.put("formatted_address", formatted_address);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longtitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;

    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();
        List<HashMap<String, String>> nearbyPlaceList = new ArrayList<>();
        HashMap<String, String> nearbyPlaceMap = null;

        for (int i = 0; i < counter; i++) {
            try {
                nearbyPlaceMap = getSinglePlace((JSONObject) jsonArray.get(i));
                nearbyPlaceList.add(nearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return nearbyPlaceList;
    }

    public List<HashMap<String,String>> parse(String jSONdata){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }


}


