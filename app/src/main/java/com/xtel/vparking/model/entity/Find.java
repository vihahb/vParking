package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by vivhp on 12/9/2016.
 */

public class Find implements Serializable {
    @Expose
    private int type;
    @Expose
    private int price;
    @Expose
    private int price_type;
    @Expose
    private String begin_time;
    @Expose
    private String end_time;

    public Find(int type, int price, int price_type, String begin_time, String end_time) {
        this.type = type;
        this.price = price;
        this.price_type = price_type;
        this.begin_time = begin_time;
        this.end_time = end_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice_type() {
        return price_type;
    }

    public void setPrice_type(int price_type) {
        this.price_type = price_type;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
