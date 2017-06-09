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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    static public boolean generate = false;
    static public boolean check = false;
    static public LatLng dest = null;
    static public int completions = 0;
    static public double initDistance = 0.88;
    static private String initDistanceStr = "0.88";
    static public boolean newDest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences data = getPreferences(MODE_PRIVATE);
        // Default destination is the Portlandia statue
        double destLat = data.getFloat("destLat", (float) 45.51575);
        double destLon = data.getFloat("destLon", (float) -122.679028);
        dest = new LatLng(destLat, destLon);
        completions = data.getInt("completions", 0);
        initDistanceStr = data.getString("initDistanceStr","0.88");
        newDest = data.getBoolean("newDest", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_legal) {
            startActivity(new Intent(this, LegalInfoActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onGenerateClick(View v) {
        generate = true;
        check = false;
        newDest = true;
        EditText miles = (EditText) findViewById(R.id.distance);
        initDistance = Double.parseDouble(miles.getText().toString());
        startActivity(new Intent(this, StreetViewPanoramaBasicDemoActivity.class));
    }

    public void onViewClick(View v) {
        generate = false;
        check = false;
        startActivity(new Intent(this, StreetViewPanoramaBasicDemoActivity.class));
    }

    public void onFoundClick(View v) {
        generate = false;
        check = true;
        startActivity(new Intent(this, StreetViewPanoramaBasicDemoActivity.class));
    }

    static public boolean getGenerate() {
        return generate;
    }

     protected void onPostCreate(Bundle result) {
        super.onPostCreate(result);
         EditText distance = (EditText) findViewById(R.id.distance);
         distance.setText(initDistanceStr);
         updateProgress();
    }

    void updateProgress() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setProgress(completions);
        bar.setScaleY(5F);
        TextView text = (TextView) findViewById(R.id.progressText);
        if(completions==0) {
            text.setText("");
        } else if(completions > 4) {
            text.setText(String.format(getString(R.string.excited_progress), completions));
        } else if(completions!=1) {
            text.setText(String.format(getString(R.string.variable_progress), completions));
        } else {
            text.setText(R.string.first_progress);
        }
        findViewById(R.id.checkButton).setEnabled(newDest);
    }

    public void resetProgress(View v) {
        completions = 0;
        updateProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgress();
    }

    protected void onStop(){
        super.onStop();
        SharedPreferences data = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putFloat("destLat", (float) dest.latitude);
        editor.putFloat("destLon", (float) dest.longitude);
        editor.putInt("completions",completions);
        EditText distance = (EditText) findViewById(R.id.distance);
        editor.putString("initDistanceStr", distance.getText().toString());
        editor.putBoolean("newDest", newDest);
        editor.commit();
    }
}
