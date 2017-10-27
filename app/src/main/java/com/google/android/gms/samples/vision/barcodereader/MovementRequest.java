package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.samples.vision.barcodereader.view.SlidingTabsColorsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//Framework
import framework.library.common.helper.date.DateHelper;

public class MovementRequest extends AppCompatActivity  {
    ListView listView ;
    ArrayList<HashMap<String,String>> movementlist;
    SharedPreferences user;
    static String departmentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_request);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SlidingTabsColorsFragment fragment = new SlidingTabsColorsFragment();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();

    }
}
