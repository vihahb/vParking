package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Lê Công Long Vũ on 12/14/2016.
 */

public class CheckInVerhicle implements Serializable {
    @Expose
    private String parking_code;
    @Expose
    private int checkin_type;
    @Expose
    private int verhicle_id;

    public CheckInVerhicle() {
    }

    public String getParking_code() {
        return parking_code;
    }

    public void setParking_code(String parking_code) {
        this.parking_code = parking_code;
    }

    public int getCheckin_type() {
        return checkin_type;
    }

    public void setCheckin_type(int checkin_type) {
        this.checkin_type = checkin_type;
    }

    public int getVerhicle_id() {
        return verhicle_id;
    }

    public void setVerhicle_id(int verhicle_id) {
        this.verhicle_id = verhicle_id;
    }
}
