package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.HashMap;

public class MovementRequest extends AppCompatActivity {
    ListView listView ;
    ArrayList<HashMap<String,String>> movementlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_request);
        movementlist = new ArrayList<>();
        listView = (ListView) findViewById(R.id.movementlist);
        new IntitTask().execute();
    }
    public class IntitTask extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... urls) {

            String jsonString = makeServiceCall();
            if (jsonString != "") {
                try {
                    JSONObject data = new JSONObject(jsonString);
                    JSONArray jsonArray = data.optJSONArray("obj");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jb = jsonArray.getJSONObject(i);
                        String ID = jb.optString("ID");
                        String Description = jb.optString("Description");
                        String Location = jb.optString("LocationName");
                        String MovementDate = jb.optString("MovementDate");



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
                    new int[] {R.id.id,R.id.description,R.id.Location,R.id.movementdate}
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
            URL url = new URL("http://192.168.1.5:8080/api/movementrequest/getall");
            HttpURLConnection urlConnection = (HttpURLConnection   ) url.openConnection();
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
