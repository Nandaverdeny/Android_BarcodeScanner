package com.escurity.asset.management.model;

import java.util.Date;

/**
 * Created by escurity on 10/18/2017.
 */

public class DataModel {
    private  String DetailID;
    private  String AssetCategory;
    private  String Description;
    private  String Qty;
    private  String RequestTo;
    private  String Transfered;

    public DataModel(String detailID,String assetCategory,String description,String qty, String requestTo,String transfered)
    {
        this.DetailID = detailID;
        this.AssetCategory = assetCategory;
        this.Description = description;
        this.Qty = qty;
        this.RequestTo = requestTo;
        this.Transfered = transfered;
    }

    public  String GetDetailID()
    {
        return  DetailID;
    }
    public void setDetailID(String detailID) {
        this.DetailID = detailID;
    }
    public  String GetAssetCategory()
    {
        return  AssetCategory;
    }
    public void setAssetCategory(String assetCategory) {
        this.AssetCategory = assetCategory;
    }
    public  String GetDescription()
    {
        return  Description;
    }
    public void setDescription(String description) {
        this.Description = description;
    }
    public  String GetQty()
    {
        return  Qty;
    }
    public void setQty(String qty) {
        this.Qty = qty;
    }
    public  String GetRequestTo()
    {
        return  RequestTo;
    }
    public void setRequestTo(String requestTo) {
        this.RequestTo = requestTo;
    }
    public  String GetTransfered()
    {
        return  Transfered;
    }
    public void setTransfered(String transfered) {
        this.Transfered = transfered;
    }

}
