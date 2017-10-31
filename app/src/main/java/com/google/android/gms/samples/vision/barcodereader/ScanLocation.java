package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ScanLocation extends Activity implements View.OnClickListener {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private static String LocationBarcode ;
    private  TextView locationBarcodetextView;
    public  String LocationID;
    public  String LocationName;

    public static void addItem(String item)
    {
        LocationBarcode = item;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_location);



        locationBarcodetextView = (TextView)findViewById(R.id.locationBarcodeTextView);
        locationBarcodetextView.setText(LocationBarcode);


        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        statusMessage = (TextView) findViewById(R.id.status_message);
        findViewById(R.id.read_barcode).setOnClickListener(this);
        SubmitClick();
    }
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {

            // launch barcode activity.
            Intent intent = new Intent(this, LocationBarcodeCapture.class);
            intent.putExtra(LocationBarcodeCapture.AutoFocus, autoFocus.isChecked());
            intent.putExtra(LocationBarcodeCapture.UseFlash, useFlash.isChecked());
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    ArrayList<Barcode> listBarcode = data.getParcelableArrayListExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    locationBarcodetextView.setText(LocationBarcode);
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

    public  void SubmitClick ()
    {
        Button button = (Button)findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener(){
            public  void  onClick (View v) {
        try {
            new GetLocation().execute();
        }
        catch (Exception e) {
        }
            }
        });
    }
    public class GetLocation extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... urls) {

            String jsonString = makeServiceCall();
            if (jsonString != null) {
                try {
                    JSONObject data = new JSONObject(jsonString);
                    JSONObject obj = data.getJSONObject("obj");
                    JSONObject Location = obj.getJSONObject("Location");
                    LocationID = Location.optString("ID");
                    LocationName = Location.optString("Name");

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
            if(LocationID != null)
            {
                Intent intent = new Intent(ScanLocation.this,AssetOpnameActivity.class);
                intent.putExtra("LocationID",LocationID);
                intent.putExtra("LocationName",LocationName);
                startActivity(intent);
            }
            else
            {
                statusMessage.setText("The Location barcode is not found in system");
            }

        }

    }

    public  String makeServiceCall(){
        try {
            URL url = new URL("http://escurity001:1130/api/location/getlocationbycode/" + LocationBarcode );
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
