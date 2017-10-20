package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    public  String locationID ;
    public  String  ID ;
    public  String  Name ;
    HashMap<String, String> mv = new HashMap<>();
    TextView textViewID;
    TextView textViewDescription;
    TextView textViewLocation;
    TextView textViewMovementDate;
    TextView textViewNotes;
    TextView textViewLocationID;
    ListView listView ;
    Button scanbutton;

    public  String  IDMv ;
    public  String  Description ;
    public  String  LocationID ;
    public  String  Location ;
    public  String  MovementDate ;
    public  String  Notes ;

    ArrayList<HashMap<String,String>> movementdetaillist;
    ArrayList<DataModel> modelArrayList;
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
        textViewLocationID = (TextView)findViewById(R.id.textViewLocationID);

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
                        modelArrayList = new ArrayList<DataModel>();
                        modelArrayList.add(new DataModel(id,assetcategory,description,qty,requestto,transfered));
                        mvdetail.put("iddetail", id);
                        mvdetail.put("assetcategory", assetcategory);
                        mvdetail.put("description", description);
                        mvdetail.put("qty",qty)     ;
                        mvdetail.put("requestto",requestto);
                        mvdetail.put("transfered",transfered);

                        movementdetaillist.add(mvdetail);

                    }
                    JSONObject movement = jsonObject.getJSONObject("MovementRequest");
                    IDMv = movement.getString("ID");
                    Description = movement.getString("Description");
                    MovementDate = movement.getString("MovementDate");
                    Location = movement.getString("LocationName");
                    Notes = movement.getString("Notes");
                    LocationID = movement.getString("LocationID");

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
            textViewLocationID.setText(LocationID);
            
            CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);

        }

    }

    public  String makeServiceCall(int id){
        try {
            URL url = new URL("http://escurity001:1130/api/movementrequest/getmovementrequestbyid/"+ id);
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

    public class CustomAdapter extends BaseAdapter {

        ArrayList<DataModel> listArray;

        public  CustomAdapter()
        {
            listArray =new ArrayList<>();
            listArray = modelArrayList;

        }
        public  int getCount()
        {
            return  listArray.size();
        }

        public  Object getItem(int i)
        {
            return  listArray.get(i);
        }
        public  long getItemId(int i)
        {
            return  i;
        }

        public View getView(int index, View view, final ViewGroup parent)
        {
            if(view == null)
            {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(R.layout.movementdetail_item,parent,false);
            }

            final DataModel dataModel = listArray.get(index);
            Button scanbutton = (Button)view.findViewById(R.id.scanbutton);
            TextView editTextIDDetail = (TextView)view.findViewById(R.id.editTextIDDetail);
            editTextIDDetail.setText(dataModel.GetDetailID());

            TextView editTextAssetCategory = (TextView)view.findViewById(R.id.editTextAssetCategory);
            editTextAssetCategory.setText(dataModel.GetAssetCategory());

            TextView editTextDescription = (TextView)view.findViewById(R.id.editTextDescription);
            editTextDescription.setText(dataModel.GetDescription());

            TextView editTextQty = (TextView)view.findViewById(R.id.editTextQty);
            editTextQty.setText(dataModel.GetQty());

            TextView editTextTransfered = (TextView)view.findViewById(R.id.editTextTransfered);
            editTextTransfered.setText(dataModel.GetTransfered());


            scanbutton.setOnClickListener(new View.OnClickListener()
            {
                public  void  onClick (View v)
                {
                    String detaiid = dataModel.GetDetailID();
                    String locationid =  textViewLocationID.getText().toString();
                    String movementid = textViewID.getText().toString();
                    Intent intent = new Intent(MovementRequestDetails.this, MainActivity.class);
                    intent.putExtra("detailid", detaiid);
                    intent.putExtra("locationid",locationid);
                    intent.putExtra("movementid",movementid);
                    startActivity(intent);
                }
            });
            return  view;
        }

    }




}
