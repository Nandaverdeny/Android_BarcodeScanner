package com.escurity.asset.management;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class SplashActivity extends AppCompatActivity {
    private  static  int splash = 2000;
    public  String username ="";
    public  String password ="";
    SharedPreferences sharedpreferences;
    public static final String UserStore = "UserStore" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                SharedPreferences user = getSharedPreferences("UserStore",MODE_PRIVATE);

                 username = user.getString("Username",null);
                 password = user.getString("Password",null);



                if(username != null && password != null)
                {
                    new UserLoginTask().execute();
                }
                else
                {

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                }

                this.finish();
            }
            private void finish() {
            }
        }, splash);
    }

    public class UserLoginTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return null;
            }
            try
            {
                URL url = new URL("http://escurity001:1131/API/user/login");
                JSONObject object = new JSONObject();
                object.put("Username",username);
                object.put("Password",password);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(1500);
                connection.setConnectTimeout(1500);
                connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                connection.setDoInput(true);
                connection.setDoOutput(true);



                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(object.toString());
                os.flush();
                os.close();

                int responseCode=connection.getResponseCode();
                connection.getContent();

                if (responseCode == HttpsURLConnection.HTTP_OK) {


                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            connection.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    return new String("login failed");


                }

            }
            catch (Exception e){
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                return new String("Exception: " + e.getMessage());
            }






            // TODO: register the new account here.

        }


        protected void onPostExecute(final String result) {


            try
            {
                JSONObject object = new JSONObject(result);
                JSONObject objectresult = new JSONObject(object.getString("obj"));
                String success = objectresult.getString("result");
                if(success == "true")
                {
                    finish();
                    sharedpreferences = getSharedPreferences(UserStore, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("Username",objectresult.getString("Username"));
                    editor.putString("Password",password);
                    editor.putString("DepartmentID",objectresult.getString("DepartmentID"));
                    editor.commit();

                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Success False",
                        Toast.LENGTH_LONG).show();
            }


        }


    }
}
