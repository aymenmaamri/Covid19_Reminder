package com.example.covid19_reminder.yichen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetStationList extends AsyncTask<Object, String, String> {
    private  String googlePlaceData,url;
    private SharedPreferences sharedPreferences;


    public Set<String> getStations() {
        return stations;
    }

    private Set<String> stations = new HashSet<String>();

    @Override
    protected String doInBackground(Object... objects) {
        sharedPreferences = (SharedPreferences)objects[0];
        url = (String)objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlaces = null;
        DataParser dataParser = new DataParser();
        nearbyPlaces = dataParser.parse(s);
        getStationList(nearbyPlaces);
    }

    public void getStationList(List<HashMap<String,String>> nearbyPlaces){
        for( int i=0; i<nearbyPlaces.size();i++){
            HashMap<String,String> googleNearbyPlace = nearbyPlaces.get(i);
            String formatted_address = googleNearbyPlace.get("formatted_address");
            stations.add(formatted_address);
        }
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putStringSet("StationAddress",stations);
        e.apply();
    }

}
