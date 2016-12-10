package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class Verhicle {

    @Expose
    private int id;
    @Expose
    private String plate_number;
    @Expose
    private int type;
    @Expose
    private String name;
    @Expose
    private String desc;
    @Expose
    private int flag_default;
    @Expose
    private Brandname brandname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFlag_default() {
        return flag_default;
    }

    public void setFlag_default(int flag_default) {
        this.flag_default = flag_default;
    }

    public Brandname getBrandname() {
        return brandname;
    }

    public void setBrandname(Brandname brandname) {
        this.brandname = brandname;
    }
}
