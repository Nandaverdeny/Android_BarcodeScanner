package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StockOpnameListActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String>> arrayList;
    ArrayList<AssetOpnameModel> assetOpnameModelArrayList;
    ListView assetopnamelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_opname_list);
        Intent intent = getIntent();
        ArrayList<String> assetlist = intent.getStringArrayListExtra("List");




        assetopnamelist = (ListView) findViewById(R.id.assetopname);

        convertStringToJson(assetlist);
        ScanAssetOpname();

    }

    public  void  convertStringToJson(ArrayList<String> list)
    {
        if(list.size() != 0)
        {
            try {
                JSONArray  jsonArray= new JSONArray(list);
                assetOpnameModelArrayList = new ArrayList<AssetOpnameModel>();
                for (int i = 0 ; i < jsonArray.length(); i++ )
                {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String ID = obj.optString("ID");
                    String AssetName = obj.optString("AssetName");
                    String AssetBarcode = obj.optString("AssetBarcode");
                    boolean IsOpname = obj.optBoolean("isOpname");
                    String AssetID = obj.optString("AssetID");
                    assetOpnameModelArrayList.add(new AssetOpnameModel(ID,AssetID,AssetName,AssetBarcode,IsOpname));
                }


                CustomAdapter customAdapter = new CustomAdapter();
                assetopnamelist.setAdapter(customAdapter);
            }
            catch (JSONException e){

            }
        }
    }


    public class CustomAdapter extends BaseAdapter {
        ArrayList<AssetOpnameModel> listArray;

        public CustomAdapter() {
            listArray = new ArrayList<>();
            listArray = assetOpnameModelArrayList;
        }

        public int getCount() {
            return listArray.size();
        }

        public Object getItem(int i) {
            return listArray.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(int index, View view, final ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(R.layout.asset_opname_item, parent, false);
            }
            final AssetOpnameModel dataModel = listArray.get(index);


            TextView textviewAssetName = (TextView)view.findViewById(R.id.textviewAssetName);
            textviewAssetName.setText(dataModel.GetAssetName());

            TextView textViewAssetBarcode = (TextView)view.findViewById(R.id.textViewAssetBarcode);
            textViewAssetBarcode.setText(dataModel.GetAssetBarcode());


            return view;

        }

    }
    public  void  ScanAssetOpname()
    {
        Button button = (Button)findViewById(R.id.Scan);
        button.setOnClickListener(new View.OnClickListener()
        {
            public  void  onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), ScanAssetActivity.class);
                startActivity(i);
            }
        });
    }
}
