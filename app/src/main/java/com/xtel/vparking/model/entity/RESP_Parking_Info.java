package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import vn.xtel.quanlybaido.model.PicturesModel;
import vn.xtel.quanlybaido.model.PricesModel;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class RESP_Parking_Info {
    @Expose
    private int id;
    @Expose
    private double uid;
    @Expose
    private double lat;
    @Expose
    private double lng;
    @Expose
    private double type;
    @Expose
    private double status;
    @Expose
    private String code;
    @Expose
    private String begin_time;
    @Expose
    private String end_time;
    @Expose
    private String address;
    @Expose
    private String parking_name;
    @Expose
    private String parking_desc;
    @Expose
    private String total_place;
    @Expose
    private String empty_number;
    @Expose
    private String qr_code;
    @Expose
    private String bar_code;
    @Expose
    private ArrayList<PricesModel> prices;
    @Expose
    private ArrayList<PicturesModel> pictures;
    @Expose
    private int favorite;
}