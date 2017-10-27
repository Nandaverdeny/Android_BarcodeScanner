package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_request);
        movementlist = new ArrayList<>();
        listView = (ListView) findViewById(R.id.movementlist);
        new InitTask().execute();
    }
    public class InitTask extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... urls) {

            String jsonString = makeServiceCall();
            if (jsonString !=   null) {
                try {
                    JSONObject data = new JSONObject(jsonString);
                    JSONArray jsonArray = data.optJSONArray("obj");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jb = jsonArray.getJSONObject(i);
                        JSONObject movement = jb.getJSONObject("MovementRequest");

                        String ID = movement.optString("ID");
                        String Description = movement.optString("Description");
                        String Location = movement.optString("LocationName");
                       //String MovementDate = movement.optString("MovementDate");


                        /*SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        SimpleDateFormat dest = new SimpleDateFormat("EEE dd/MMM/yyyy");
                        Date date = null;
                        try {
                            date = sdf.parse(movement.optString("MovementDate"));
                        } catch (Exception ex) {
                            Log.e("ERROR", ex.getMessage(), ex);
                        }

                        String MovementDate = dest.format(date);*/

                        String MovementDate = DateHelper.ConvertToStringDate(movement.optString("MovementDate"), "ddMMyyyy", "EEE dd/MM/yyyy");

                        HashMap<String, String> mv = new HashMap<>();
                        mv.put("id", ID);
                        mv.put("description", Description);
                        mv.put("location",Location)     ;
                        mv.put("movementdate",MovementDate);


                        movementlist.add(mv);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
            return  null;
        }




        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(
                    MovementRequest.this,movementlist,
                    R.layout.list_item,new String[] {"id","description","location","movementdate"},
                    new int[] {R.id .id,R.id.description,R.id.Location,R.id.movementdate}
            );
            listView.setAdapter(adapter);
            final ListAdapter A = adapter;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //tView listtext = (TextView) findViewById(R.id.id);
                    //String  text = listView.getItemAtPosition(i).toString();
                    HashMap<String, Object> obj = (HashMap<String, Object>) A.getItem(i);
                    String text= (String) obj.get("id");

                    Intent intent = new Intent(MovementRequest.this, MovementRequestDetails.class);
                    intent.putExtra("id", text);
                    startActivity(intent);
                }
            });

        }

    }

    public  String makeServiceCall(){
        try {
            SharedPreferences user = getSharedPreferences("UserStore",MODE_PRIVATE);

            String departmentid = user.getString("DepartmentID",null);
            URL url;
            if(departmentid != null)
            {
                url = new URL("http://Escurity001:1330/api/movementrequest/getmovementrequesttomovebydepartment/"+ Integer.parseInt(departmentid));


            }
            else
            {
                url = new URL("http://escurity001:1130/api/movementrequest/getmovementrequesttomove");
            }


            HttpURLConnection urlConnection = (HttpURLConnection   ) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage(), ex);
            return null;
        }

    }
}
