package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

public class ScanAssetActivity extends AppCompatActivity implements View.OnClickListener {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private static ArrayList<String> listItems = new ArrayList<String>();
    private static ArrayAdapter<String> adapter;
    private ListView AssetBarcodeListView;

    public static void addItem(String item)
    {
        listItems.add(item);

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

}
