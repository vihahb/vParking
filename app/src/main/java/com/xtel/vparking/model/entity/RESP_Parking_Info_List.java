package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class RESP_Parking_Info_List extends RESP_Basic {
    @Expose
    private ArrayList<ParkingInfo> data;

    public ArrayList<ParkingInfo> getData() {
        return data;
    }

    public void setData(ArrayList<ParkingInfo> data) {
        this.data = data;
    }
}