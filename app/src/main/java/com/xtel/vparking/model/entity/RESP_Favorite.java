package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/5/2016.
 */

public class RESP_Favorite extends RESP_Basic {

    @Expose
    private ArrayList<Favotire> data;

    public ArrayList<Favotire> getData() {
        return data;
    }

    public void setData(ArrayList<Favotire> data) {
        this.data = data;
    }
}
