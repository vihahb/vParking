package com.xtel.vparking.model.entity;

/**
 * Created by Lê Công Long Vũ on 12/8/2016.
 */

public class Price {
    private int money;
    private int transport_type;
    private int time_type;
    private boolean isAdd;

    public Price(int money, int transport_type, int time_type, boolean isAdd) {
        this.money = money;
        this.transport_type = transport_type;
        this.time_type = time_type;
        this.isAdd = isAdd;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getTransport_type() {
        return transport_type;
    }

    public void setTransport_type(int transport_type) {
        this.transport_type = transport_type;
    }

    public int getTime_type() {
        return time_type;
    }

    public void setTime_type(int time_type) {
        this.time_type = time_type;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }
}
