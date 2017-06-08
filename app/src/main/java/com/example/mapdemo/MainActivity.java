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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    static public boolean generate = false;
    static public LatLng dest = null;
    static private int completions = 0;
    //TextView progressText = (TextView)findViewById(R.id.progressText);
    //ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);


    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            Resources resources = getContext().getResources();
            String title = resources.getString(demo.titleId);
            String description = resources.getString(demo.descriptionId);
            featureView.setContentDescription(title + ". " + description);

            return featureView;
        }
    }

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
        //progressText.setText("My Awesome Text");
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
        DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(position);
        startActivity(new Intent(this, demo.activityClass));
    }

    public void onGenerateClick(View v) {
        generate = true;
        startActivity(new Intent(this, StreetViewPanoramaBasicDemoActivity.class));
    }

    public void onViewClick(View v) {
        generate = false;
        startActivity(new Intent(this, StreetViewPanoramaBasicDemoActivity.class));
    }

    public void onFoundClick(View v) {
        Toast.makeText(this, "Well done!", Toast.LENGTH_SHORT).show();
        completions = completions + 1;
        setProgress();
    }

    static public boolean getGenerate() {
        return generate;
    }

     protected void onPostCreate(Bundle result) {
        super.onPostCreate(result);
         setProgress();
    }

    void setProgress() {
        TextView text = (TextView) findViewById(R.id.progressText);
        if(completions==0) {
            text.setText("");
        } else if(completions!=1) {
            text.setText(String.format("You have reached %d destinations", completions));
        } else {
            text.setText("You have reached 1 destination");
        }
    }
}
