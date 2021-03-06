// Copyright (c) 2017 Noah Freed
// This file has been modified from its original form

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import static com.example.mapdemo.MainActivity.check;
import static com.example.mapdemo.MainActivity.completions;
import static com.example.mapdemo.MainActivity.dest;
import static com.example.mapdemo.MainActivity.generate;
import static com.example.mapdemo.MainActivity.initDistance;
import static com.example.mapdemo.MainActivity.newDest;
import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * This shows how to create a simple activity with streetview
 */
public class StreetViewPanoramaBasicDemoActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng PSU = new LatLng(45.5110, -122.6832);
    private static final double EARTHRADIUS = 3959;
    GoogleApiClient mGoogleApiClient = null;
    Location mLastLocation = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_panorama_basic_demo);
        enableMyLocation();
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            Log.d("onCreate", "Creating GoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("onConnected", "Connected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d("onConnected", "Have permission");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("onConnected", "mLastLocation notnull");
            Log.d("Lat",String.valueOf(mLastLocation.getLatitude()));
            Log.d("Lon",String.valueOf(mLastLocation.getLongitude()));
        }
        findDestination();
        if(MainActivity.check) {
            check = false;
            checkDestination(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    private void findDestination() {
        if(MainActivity.getGenerate()) {
            LatLng start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            double angle = 2 * PI * random();
            double distance = initDistance * 6/7 + initDistance * 2/7 * random();
            double startLatRad = start.latitude * PI / 180;
            double startLngRad = start.longitude * PI / 180;

            // Distance to LatLng calculation based on "Destination point given distance and bearing from start point" formula
            // at http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness
            double destLatRad = Math.asin(Math.sin(startLatRad) * Math.cos(distance / EARTHRADIUS) +
                    Math.cos(startLatRad) * Math.sin(distance / EARTHRADIUS) * Math.cos(angle));
            double destLngRad = startLngRad + Math.atan2(Math.sin(angle) * Math.sin(distance / EARTHRADIUS) * Math.cos(startLatRad),
                    Math.cos(distance / EARTHRADIUS) - Math.sin(startLatRad) * Math.sin(destLatRad));

            double destLat = destLatRad * 180 / PI;
            double destLng = destLngRad * 180 / PI;
            final LatLng destination = new LatLng(destLat, destLng);
            Log.d("Destination:", destination.toString());
            dest = destination;
        }
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        panorama.setPosition(dest, 50);
                        panorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                            @Override
                            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                                if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                                    // location is present
                                    Log.d("onPanoramaChange","present");
                                    generate = false;
                                } else {
                                    // location not available
                                    Log.d("onPanoramaChange","absent");
                                    findDestination();
                                }
                            }
                        });
                    }
                });
    }

    private void checkDestination(double latitude, double longitude){
        //double MAXOFFSET = 100/111319.9; //double the radius for finding panorama in meters divided by max # of meters in a degree
        //if(dest.latitude - MAXOFFSET < latitude && latitude < dest.latitude + MAXOFFSET &&
        //        dest.longitude - MAXOFFSET < longitude && longitude < dest.longitude + MAXOFFSET){
        float result[] = new float[1];
        Location.distanceBetween(latitude, longitude, dest.latitude, dest.longitude, result);
        if(result[0] < 100){
            Toast.makeText(this, "Well done!", Toast.LENGTH_SHORT).show();
            completions = completions + 1;
            newDest = false;
        } else {
            Toast.makeText(this, "Not close enough", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}