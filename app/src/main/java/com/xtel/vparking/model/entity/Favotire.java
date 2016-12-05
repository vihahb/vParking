package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by Mr. M.2 on 12/5/2016.
 */

public class Favotire {
    @Expose
    private int id;
    @Expose
    private String parking_name;
    @Expose
    private String address;
    @Expose
    private String begin;
    @Expose
    private String end;
    @Expose
    private double price;
    @Expose
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParking_name() {
        return parking_name;
    }

    public void setParking_name(String parking_name) {
        this.parking_name = parking_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
