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

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * This shows how to create a simple activity with streetview
 */
public class StreetViewPanoramaBasicDemoActivity extends AppCompatActivity {

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng PSU = new LatLng(45.5110, -122.6832);
    private static final double EARTHRADIUS = 3959;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_panorama_basic_demo);
        LatLng start = PSU;
        double angle = 2 * PI * random();
        double distance = .7525 + .2508 * random();
        double startLatRad = start.latitude * PI/180;
        double startLngRad = start.longitude * PI/180;

        // Distance to LatLng calculation based on "Destination point given distance and bearing from start point" formula
        // at http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness
        double destLatRad =  Math.asin( Math.sin(startLatRad)*Math.cos(distance/EARTHRADIUS) +
                Math.cos(startLatRad)*Math.sin(distance/EARTHRADIUS)*Math.cos(angle) );
        double destLngRad = startLngRad + Math.atan2(Math.sin(angle)*Math.sin(distance/EARTHRADIUS)*Math.cos(startLatRad),
                Math.cos(distance/EARTHRADIUS)-Math.sin(startLatRad)*Math.sin(destLatRad));

        double destLat = destLatRad * 180/PI;
        double destLng = destLngRad * 180/PI;
        final LatLng destination = new LatLng(destLat, destLng);
        Log.d("Destination:", destination.toString());

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        // Only set the panorama to SYDNEY on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        if (savedInstanceState == null) {
                            panorama.setPosition(destination);
                        }
                    }
                });
    }
}