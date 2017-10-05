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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MovementRequestDetails extends AppCompatActivity {
    public  String  id ;
    public  String  ID ;
    public  String  Name ;
    HashMap<String, String> mv = new HashMap<>();
    TextView textViewID;
    TextView textViewDescription;
    TextView textViewLocation;
    TextView textViewMovementDate;
    TextView textViewNotes;
    ListView listView ;

    public  String  IDMv ;
    public  String  Description ;
    public  String  Location ;
    public  String  MovementDate ;
    public  String  Notes ;

    ArrayList<HashMap<String,String>> movementdetaillist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_request_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        id =   i.getStringExtra("id");
        new IntitTask().execute();
        movementdetaillist = new ArrayList<>();
        textViewID = (TextView)findViewById(R.id.textViewID);
        textViewDescription = (TextView)findViewById(R.id.textViewDescription);
        textViewLocation = (TextView)findViewById(R.id.textViewlocation);
        textViewMovementDate = (TextView)findViewById(R.id.textViewmovementdate);
        textViewNotes = (TextView)findViewById(R.id.textViewNotes);
        listView  = (ListView)findViewById(R.id.viewmovementdetail);

        textViewDescription.setEnabled(false);
        textViewLocation.setEnabled(false);
        textViewMovementDate.setEnabled(false);
        textViewNotes.setEnabled(false);
    }
    public class IntitTask extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... urls) {

            Integer mvid = Integer.parseInt(id);
            String jsonString = makeServiceCall(mvid);
            if (jsonString != "") {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("obj");
                    JSONArray jsonArray = jsonObject.optJSONArray("MovementRequestDetail");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jb = jsonArray.getJSONObject(i);
                        HashMap<String, String> mv = new HashMap<>();
                        String id = jb.optString("ID");
                        String assetcategory = jb.optString("CategoryCDName");
                        String description = jb.optString("Description");
                        String qty = jb.optString("Quantity");
                        String requestto = jb.optString("RequestToName");
                        String transfered = jb.optString("Transfered");

                        HashMap<String, String> mvdetail = new HashMap<>();
                        mvdetail.put("iddetail", id);
                        mvdetail.put("assetcategory", assetcategory);
                        mvdetail.put("description", description);
                        mvdetail.put("qty",qty)     ;
                        mvdetail.put("requestto",requestto);
                        mvdetail.put("transfered",transfered);

                        movementdetaillist.add(mvdetail);

                    }
                    IDMv = jsonObject.getString("ID");
                    Description = jsonObject.getString("Description");
                    MovementDate = jsonObject.getString("MovementDate");
                    Location = jsonObject.getString("LocationName");
                    Notes = jsonObject.getString("Notes");

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
            textViewID.setText(IDMv);
            textViewDescription.setText(Description);
            textViewLocation .setText(Location);
            textViewMovementDate.setText(MovementDate);
            textViewNotes.setText(Notes);

            ListAdapter adapter = new SimpleAdapter(
                    MovementRequestDetails.this,movementdetaillist,
                    R.layout.movementdetail_item,new String[]
                    {"iddetail","assetcategory","description","qty","transfered"},
                    new int[] {R.id.editTextIDDetail,R.id.editTextAssetCategory, R.id.editTextDescription,R.id.editTextQty,R.id.editTextTransfered}
            );
            listView.setAdapter(adapter);
            final ListAdapter finaladapter = adapter;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) finaladapter.getItem(i);
                    String text= (String) obj.get("iddetail");

                    Intent intent = new Intent(MovementRequestDetails.this, MainActivity.class);
                    intent.putExtra("iddetail", text);
                    startActivity(intent);
                }
            });
        }

    }
    public  String makeServiceCall(int id){
        try {
            URL url = new URL("http://192.168.1.5:8080/api/movementrequest/get/"+ id);
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
