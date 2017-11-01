package com.escurity.asset.management;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.escurity.asset.management.R;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
     {
         Button button;
         SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // Set Drawer Icon Color
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorPrimary));
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        InitializeMovementRequest();
        InitializeStockOpname();


        }
         @Override
         public void onBackPressed() {
             DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
             if (drawer.isDrawerOpen(GravityCompat.START)) {
                 drawer.closeDrawer(GravityCompat.START);
             } else {
                 super.onBackPressed();
             }
         }

         @Override
         public boolean onCreateOptionsMenu(Menu menu) {
             // Inflate the menu; this adds items to the action bar if it is present.
             getMenuInflater().inflate(R.menu.main, menu);
             SharedPreferences user = getSharedPreferences("UserStore",MODE_PRIVATE);
             String username = user.getString("Username",null);
             TextView Username = (TextView)findViewById(R.id.username);
             String usernameshow = username + " !";
             Username.setText(usernameshow);
             return true;
         }

         @Override
         public boolean onOptionsItemSelected(MenuItem item) {
             // Handle action bar item clicks here. The action bar will
             // automatically handle clicks on the Home/Up button, so long
             // as you specify a parent activity in AndroidManifest.xml.
             int id = item.getItemId();

             //noinspection SimplifiableIfStatement
             if (id == R.id.action_settings) {
                 return true;
             }

             return super.onOptionsItemSelected(item);
         }

         @SuppressWarnings("StatementWithEmptyBody")
         @Override
         public boolean onNavigationItemSelected(MenuItem item) {
             // Handle navigation view item clicks here.
             int id = item.getItemId();

             if (id == R.id.nav_home) {
                 Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                 startActivity(intent);
             } else if (id == R.id.nav_assetmovement) {
                 Intent intent = new Intent(HomeActivity.this,MovementRequest.class);
                 startActivity(intent);

             } else if (id == R.id.nav_assetopname) {
                 Intent intent = new Intent(HomeActivity.this,ScanLocationActivity.class);
                 startActivity(intent);

             }
             else if( id == R.id.nav_manage)
             {
                 sharedpreferences = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
                 SharedPreferences.Editor editor = sharedpreferences.edit();
                 editor.clear();
                 editor.commit();
                 finish();
                 editor.clear();

                 Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                 startActivity(intent);
             }

             DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
             drawer.closeDrawer(GravityCompat.START);
             return true;
         }


         public  void  InitializeStockOpname()
         {
             button = (Button)findViewById(R.id.stockopname);
             button.setOnClickListener(new View.OnClickListener()
             {
                 public  void  onClick(View v)
                 {
                     Intent i = new Intent(getApplicationContext(), ScanLocationActivity.class);
                     startActivity(i);
                 }
             });
         }




         public  void  InitializeMovementRequest(){
         button = (Button)findViewById(R.id.movementrequest);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(getApplicationContext(), MovementRequest.class);

                 startActivity(i);
             }
         });


    }

}
