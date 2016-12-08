package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by vivhp on 12/8/2016.
 */

public class RESP_FLAG extends RESP_Basic{
    @Expose
    public int parking_flag;


    public int getParking_flag() {
        return parking_flag;
    }

    public void setParking_flag(int parking_flag) {
        this.parking_flag = parking_flag;
    }

    @Override
    public String toString() {
        return "RESP_FLAG{" +
                "parking_flag=" + parking_flag +
                '}';
    }
}
