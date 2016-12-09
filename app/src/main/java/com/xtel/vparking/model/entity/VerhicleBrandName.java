package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by vivhp on 12/9/2016.
 */

public class VerhicleBrandName extends RESP_Basic {
    @Expose
    private String code;
    @Expose
    private String name;
    @Expose
    private String madeby;

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

    @Override
    public String toString() {
        return "VerhicleBrandName{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", madeby='" + madeby + '\'' +
                '}';
    }
}
