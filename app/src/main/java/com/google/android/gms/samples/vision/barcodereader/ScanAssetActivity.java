package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ScanAssetActivity extends AppCompatActivity implements View.OnClickListener {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private static ArrayList<String> listItems = new ArrayList<String>();
    private static ArrayAdapter<String> adapter;
    private ListView AssetBarcodeListView;
    public  String LocationID,DateOpname;
    ArrayList<String> assetlist;

    public static void addItem(String item)
    {
        if(!listItems.contains(item)) {
            listItems.add(item);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_asset);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        statusMessage = (TextView) findViewById(R.id.status_message);
        findViewById(R.id.read_barcode).setOnClickListener(this);

        AssetBarcodeListView = (ListView)findViewById(R.id.listasset);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        AssetBarcodeListView.setAdapter(adapter);

        Intent intent = getIntent();
        assetlist = intent.getStringArrayListExtra("AssetList");
        LocationID = intent.getStringExtra("LocationID");
        DateOpname = intent.getStringExtra("DateOpname");

        Submit();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {

            // launch barcode activity.
            Intent intent = new Intent(this, AssetBarcodeCapture.class);
            intent.putExtra(AssetBarcodeCapture.AutoFocus, autoFocus.isChecked());
            intent.putExtra(AssetBarcodeCapture.UseFlash, useFlash.isChecked());
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    ArrayList<Barcode> listBarcode = data.getParcelableArrayListExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    Log.d(TAG, "Barcode count: " + listBarcode.size());
                    for (int i = 0; i < listBarcode.size(); i++) {
                        //listItems.add(listBarcode.get(i).displayValue);
                        Log.d(TAG, "Barcode read: " + listBarcode.get(i).displayValue);
                    }
                    Log.d(TAG, "listItems count: " + listItems.size());
                    adapter.notifyDataSetChanged();
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public  void  Submit(){

        Button button = (Button)findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener(){
            public  void  onClick (View v) {


                try {
                    new PostData().execute();

                }
                catch (Exception e) {

                }

            }
        });
    }


    public class PostData extends AsyncTask<String, Void, String> {

        public String doInBackground(String... urls) {

            try
            {
                SharedPreferences user = getSharedPreferences("UserStore",MODE_PRIVATE);
                URL url = new URL("http://escurity001:1130/api/assetlocation/OpnameAsset");




                JSONObject object = new JSONObject();
                JSONArray assetopname = new JSONArray(listItems);
                JSONArray assetid = new JSONArray(assetlist);
                String createdby = user.getString("Username",null);
                object.put("AssetOpname",assetopname);
                object.put("AssetIDList",assetid);
                object.put("LocationID",LocationID);
                object.put("CreatedBy",createdby);

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
                    return new String("false : "+responseCode);
                }




            }
            catch ( Exception e)
            {
                return new String("Exception: " + e.getMessage());
            }

        }


        protected void onPostExecute(String result) {

            try
            {
                JSONObject object = new JSONObject(result);
                JSONObject objresult = object.getJSONObject("obj");
                String status = objresult.getString("status");

                if(status.contains("true"))
                {
                    Intent intent = new Intent(ScanAssetActivity.this,AssetOpnameActivity.class);
                    intent.putExtra("LocationID",LocationID);
                    listItems.clear();
                    startActivity(intent);
                }
                else
                {
                    String message = objresult.getString("message");
                    listItems.clear();
                    adapter.notifyDataSetChanged();
                    statusMessage.setText(message);
                    Toast.makeText(getApplicationContext(),
                            message,
                            Toast.LENGTH_LONG).show();
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
