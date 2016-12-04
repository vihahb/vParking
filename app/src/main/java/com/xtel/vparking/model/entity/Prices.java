package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Lê Công Long Vũ on 11/10/2016.
 */

public class Prices {
    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private int price;
    @Expose
    private int price_type;
    @Expose
    private int price_for;
    @Expose
    private String begin_time;
    @Expose
    private String end_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPrice_for() {
        return price_for;
    }

    public void setPrice_for(int price_for) {
        this.price_for = price_for;
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