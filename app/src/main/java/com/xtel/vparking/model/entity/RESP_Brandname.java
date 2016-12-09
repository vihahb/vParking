package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/9/2016.
 */

public class RESP_Brandname extends RESP_Basic {
    @Expose
    private ArrayList<VerhicleBrandName> data;
    @Expose
    private int version;

    public ArrayList<VerhicleBrandName> getData() {
        return data;
    }

    public void setData(ArrayList<VerhicleBrandName> data) {
        this.data = data;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RESP_Brandname{" +
                "data=" + data +
                ", version=" + version +
                '}';
    }
}
