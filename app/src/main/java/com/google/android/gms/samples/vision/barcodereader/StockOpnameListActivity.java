package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StockOpnameListActivity extends AppCompatActivity {


    ArrayList<AssetOpnameModel> assetOpnameModelArrayList;
    ListView assetopnamelist;
    public  String LocationID,OpnameDate,LocationName;
    public  int Status;
    private static ArrayList<String> AssetIDList = new ArrayList<String>();
    private ArrayList<HashMap<String,String>> AssetList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_opname_list);
        Intent intent = getIntent();
        ArrayList<String> assetlist = intent.getStringArrayListExtra("List");
        LocationID = intent.getStringExtra("LocationID");
        LocationName = intent.getStringExtra("LocationName");
        OpnameDate = intent.getStringExtra("OpnameDate");



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
                AssetIDList = new ArrayList<>();
                AssetList = new ArrayList<>();
                for (int i = 0 ; i < jsonArray.length(); i++ )
                {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String ID = obj.optString("ID");
                    String AssetName = obj.optString("AssetName");
                    String AssetBarcode = obj.optString("AssetBarcode");
                    boolean IsOpname = obj.optBoolean("IsOpname");
                    String AssetID = obj.optString("AssetID");
                    String Opname = "";
                    if(IsOpname == true)
                    {
                        Opname = "1";
                    }
                    else
                    {
                        Opname = "0";
                    }

                    assetOpnameModelArrayList.add(new AssetOpnameModel(ID,AssetID,AssetName,AssetBarcode,IsOpname));
                    AssetIDList.add(AssetID);
                    HashMap<String, String> mv = new HashMap<>();
                    mv.put("AssetBarcode",AssetBarcode);
                    mv.put("IsOpname", Opname);
                    AssetList.add(mv);



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

            ImageView imageView2 = (ImageView)view.findViewById(R.id.imageView2);
            boolean checkIsOpname = dataModel.GetIsOpname();
            if( checkIsOpname == false)
            {
                imageView2.setVisibility(View.INVISIBLE);
            }

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
                i.putExtra("LocationID",LocationID);
                i.putExtra("AssetIDList",AssetIDList);
                i.putExtra("AssetList",AssetList);
                i.putExtra("LocationName",LocationName);
                i.putExtra("OpnameDate",OpnameDate);

                startActivity(i);
            }
        });
    }
}
