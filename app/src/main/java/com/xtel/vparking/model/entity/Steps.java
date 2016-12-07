package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by Lê Công Long Vũ on 12/7/2016.
 */

public class Steps {
    @Expose
    private StartLocation start_location;
    @Expose
    private EndLocation end_location;
    @Expose
    private Polyline polyline;

    public StartLocation getStart_location() {
        return start_location;
    }

    public void setStart_location(StartLocation start_location) {
        this.start_location = start_location;
    }

    public EndLocation getEnd_location() {
        return end_location;
    }

    public void setEnd_location(EndLocation end_location) {
        this.end_location = end_location;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}
