package com.escurity.asset.management.model;

/**
 * Created by escurity on 10/24/2017.
 */

public class AssetOpnameModel {
    private String ID;
    private String AssetID;
    private String AssetName;
    private String AssetBarcode;
    private boolean IsOpname;

    public AssetOpnameModel(String id, String assetID, String assetName, String assetBarcode, boolean isOpname) {
        this.ID = id;
        this.AssetID = assetID;
        this.AssetName = assetName;
        this.AssetBarcode = assetBarcode;
        this.IsOpname = isOpname;
    }

    public String GetID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String GetAssetID() {
        return AssetID;
    }

    public void setAssetID(String assetID) {
        this.AssetID = assetID;
    }
    public String GetAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        this.AssetName = assetName;
    }
    public String GetAssetBarcode() {
        return AssetBarcode;
    }

    public void setAssetBarcode(String assetBarcode) {
        this.AssetBarcode = assetBarcode;
    }

    public boolean GetIsOpname() {
        return IsOpname;
    }

    public void setIsOpname(boolean isOpname) {
        this.IsOpname = isOpname;
    }

}
