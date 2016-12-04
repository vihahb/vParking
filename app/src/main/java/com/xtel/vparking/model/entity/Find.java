package com.xtel.vparking.model.entity;

import java.io.Serializable;

/**
 * Created by Lê Công Long Vũ on 11/12/2016.
 */

public class Find implements Serializable {
    private int type;
    private int place_empty;
    private int money;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPlace_empty() {
        return place_empty;
    }

    public void setPlace_empty(int place_empty) {
        this.place_empty = place_empty;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
