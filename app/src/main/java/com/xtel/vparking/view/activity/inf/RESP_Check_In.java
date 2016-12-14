package com.xtel.vparking.view.activity.inf;

import com.google.gson.annotations.Expose;
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.RESP_Basic;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/14/2016.
 */

public class RESP_Check_In extends RESP_Basic {
    @Expose
    private ArrayList<CheckIn> data;

    public ArrayList<CheckIn> getData() {
        return data;
    }

    public void setData(ArrayList<CheckIn> data) {
        this.data = data;
    }
}