package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/7/2016.
 */

public class Routers {
    @Expose
    private ArrayList<Legs> legs;

    public ArrayList<Legs> getLegs() {
        return legs;
    }

    public void setLegs(ArrayList<Legs> legs) {
        this.legs = legs;
    }
}
