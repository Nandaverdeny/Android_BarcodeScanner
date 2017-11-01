package com.escurity.asset.management;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.escurity.asset.management.view.SlidingTabsColorsFragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.escurity.asset.management.R;

//Framework


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
