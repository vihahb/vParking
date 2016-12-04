package com.xtel.vparking.model.entity;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Lê Công Long Vũ on 11/22/2016.
 */

public class MarkerModel {
    private Marker marker;
    private int id;
    private double lat;
    private double lng;

    public MarkerModel() {};

    public MarkerModel(Marker marker, int id, double lat, double lng) {
        this.marker = marker;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
