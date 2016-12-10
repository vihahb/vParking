package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class RESP_Verhicle extends RESP_Basic {

    @Expose
    private ArrayList<Verhicle> data;

    public ArrayList<Verhicle> getData() {
        return data;
    }

    public void setData(ArrayList<Verhicle> data) {
        this.data = data;
    }
}
