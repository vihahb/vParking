package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Lê Công Long Vũ on 11/10/2016.
 */

public class Pictures {
    @Expose
    private double id;
    @Expose
    private String url;

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
