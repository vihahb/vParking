package com.xtel.vparking.model.entity;

/**
 * Created by Lê Công Long Vũ on 11/17/2016.
 */

public class PlaceModel {
    private String address;
    private double latitude;
    private double longtitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
