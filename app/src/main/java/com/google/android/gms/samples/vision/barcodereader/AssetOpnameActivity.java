package com.google.android.gms.samples.vision.barcodereader;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
//Framework
import framework.library.common.helper.date.DateHelper;

public class AssetOpnameActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnDatePicker;
    EditText txtDate;
    TextView txtviewlocationName;
    private int mYear, mMonth, mDay;
    private String LocationID, LocationName, RecordDate;
    ArrayList<HashMap<String,String>> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_opname);

        btnDatePicker = (Button) findViewById(R.id.btn_date);
        txtDate = (EditText) findViewById(R.id.in_date);
        btnDatePicker.setOnClickListener(this);


        Intent intent = getIntent();
        LocationID = intent.getStringExtra("LocationID");
        LocationName = intent.getStringExtra("LocationName");

        txtviewlocationName = (TextView) findViewById(R.id.LocationName);
        txtviewlocationName.setText(LocationName);



        btnSubmitOnClick();
    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            RecordDate = String.valueOf(year) + "/" + String.valueOf((monthOfYear + 1)) +"/"+ String.valueOf(dayOfMonth) ;

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

    }

    public void btnSubmitOnClick() {
        Button button = (Button) findViewById(R.id.btn_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    new GetAssetOpname().execute();

                } catch (Exception e) {

                }
            }
        });
    }

    public class GetAssetOpname extends AsyncTask<String, Void, String> {
        public String doInBackground(String... urls) {

            try {

                URL url = new URL("http://escurity001:1130/api/assetlocation/getassetstockopname");
                JSONObject object = new JSONObject();
                object.put("LocationID", LocationID);
                object.put("CreatedDate", RecordDate);

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

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            connection.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        protected void onPostExecute(String result) {

            try {
                JSONObject data = new JSONObject(result);
                JSONObject object = data.getJSONObject("obj");
                JSONArray assetOpname = object.optJSONArray("assetlocation");

                arrayList = new ArrayList<>();
                for (int i = 0; i < assetOpname.length(); i++) {
                    JSONObject jb = assetOpname.getJSONObject(i);
                    String ID = jb.optString("ID");
                    String AssetName = jb.optString("AssetName");
                    String AssetBarcode = jb.optString("AssetBarcode");
                    String IsOpname = jb.optString("isOpname");
                    JSONObject asset = jb.getJSONObject("AssetLatest");
                    String AssetID = asset.optString("AssetID");

                    HashMap<String, String> obj = new HashMap<>();
                    obj.put("ID",ID);
                    obj.put("AssetID",AssetID);
                    obj.put("AssetName",AssetName);
                    obj.put("AssetBarcode",AssetBarcode);
                    obj.put("IsOpname",IsOpname);

                    arrayList.add(obj);

                }


                Intent intent = new Intent(AssetOpnameActivity.this , StockOpnameListActivity.class);
                intent.putExtra("List",arrayList);
                intent.putExtra("LocationID",LocationID);
                startActivity(intent);



            } catch (JSONException e) {

            }
        }


    }


}