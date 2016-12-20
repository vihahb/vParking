package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by vivhp on 12/19/2016.
 */

public class HistoryModel {

    @Expose
    private int parking_id;
    @Expose
    private int page;
    @Expose
    private int pagesize;
    @Expose
    private String begin;
    @Expose
    private String endtime;

    public int getParking_id() {
        return parking_id;
    }

    public void setParking_id(int parking_id) {
        this.parking_id = parking_id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    @Override
    public String toString() {
        return "HistoryModel{" +
                "parking_id=" + parking_id +
                ", page=" + page +
                ", pagesize=" + pagesize +
                ", begin='" + begin + '\'' +
                ", endtime='" + endtime + '\'' +
                '}';
    }
}
