package com.example.covid19_reminder.yichen;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private  String googlePlaceData,url;
    private GoogleMap mMap;


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
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

        DisplayNearbyPlaces(nearbyPlaces);
    }

    private void DisplayNearbyPlaces(List<HashMap<String,String>> nearbyPlaces){
        for( int i=0; i<nearbyPlaces.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googleNearbyPlace = nearbyPlaces.get(i);
            String nameOfPlaces = googleNearbyPlace.get("place_name");
            String formatted_address = googleNearbyPlace.get("formatted_address");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlaces + " : " + formatted_address);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }
}
