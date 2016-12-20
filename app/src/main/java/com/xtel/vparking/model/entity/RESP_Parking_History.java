package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/20/2016.
 */

public class RESP_Parking_History extends RESP_Basic {
    @Expose
    private ArrayList<CheckInHisObj> data;

    public ArrayList<CheckInHisObj> getData() {
        return data;
    }

    public void setData(ArrayList<CheckInHisObj> data) {
        this.data = data;
    }
}
