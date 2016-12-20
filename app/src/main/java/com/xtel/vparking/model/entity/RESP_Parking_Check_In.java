package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/14/2016.
 */

public class RESP_Parking_Check_In extends RESP_Basic {
    @Expose
    private ArrayList<ParkingCheckIn> data;

    public ArrayList<ParkingCheckIn> getData() {
        return data;
    }

    public void setData(ArrayList<ParkingCheckIn> data) {
        this.data = data;
    }
}