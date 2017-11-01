/*
 * Copyright (C) The Android Open Source Project
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

package com.escurity.asset.management;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.escurity.asset.management.barcodereader.BarcodeCaptureActivity;
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

import javax.net.ssl.HttpsURLConnection;

import com.escurity.asset.management.R;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    private ListView barcodeListView;
    private static ArrayList<String> listItems = new ArrayList<String>();



    private static ArrayAdapter<String> adapter;
    String MovementDetailID ;
    String LocationID;
    String MovementID ;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    public static void addItem(String item)
    {
        //TEST PUSH
        if (!listItems.contains(item)) {


            listItems.add(item);
            //adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);
        barcodeListView = (ListView)findViewById(R.id.list_view_barcode);



        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        barcodeListView.setAdapter(adapter);


        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        Intent i = getIntent();
        MovementDetailID =   i.getStringExtra("detailid");
        LocationID = i.getStringExtra("locationid");
        MovementID = i.getStringExtra("movementid");

        Submit();

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {

            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
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

                    //barcodeValue.setText(barcode.displayValue);
                    //Log.d(TAG, "Barcode read: " + barcode.displayValue);
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
               URL url = new URL("http://escurity001:1130/api/assetlocation/moveasset");




               JSONObject object = new JSONObject();
               JSONArray jsArray = new JSONArray(listItems);
               String createdby = user.getString("Username",null);
               object.put("listAsset",jsArray);
               object.put("locationID",LocationID);
               object.put("CreatedBy",createdby);
               object.put("MovementRequestDetailID",MovementDetailID);

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
                String success = object.getString("success");
                String message = object.getString("message");
                if(success == "true")
                {
                    Intent intent = new Intent(MainActivity.this,MovementRequestDetails.class);
                    intent.putExtra("id",MovementID );
                    listItems.clear();
                    startActivity(intent);
                }
                else
                {
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
