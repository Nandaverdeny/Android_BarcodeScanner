package com.escurity.asset.management.barcodereader;

import com.escurity.asset.management.ScanLocationActivity;
import com.escurity.asset.management.camera.GraphicOverlay;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by escurity on 10/27/2017.
 */

class BarcodeTrackerLocation implements MultiProcessor.Factory<Barcode>{
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    BarcodeTrackerLocation(GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay) {
        mGraphicOverlay = barcodeGraphicOverlay;
    }
    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        //TODO : Check barcode is loc or asset
        ScanLocationActivity.addItem(barcode.displayValue);
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new BarcodeGraphicTracker(mGraphicOverlay, graphic);
    }
}


