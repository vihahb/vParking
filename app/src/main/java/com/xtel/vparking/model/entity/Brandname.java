package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class Brandname {

    @Expose
    private String code;
    @Expose
    private String name;
    @Expose
    private String madeby;

    public Brandname() {
    }

    public Brandname(String code, String name, String madeby) {
        this.code = code;
        this.name = name;
        this.madeby = madeby;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMadeby() {
        return madeby;
    }

    public void setMadeby(String madeby) {
        this.madeby = madeby;
    }
}
