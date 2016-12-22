package com.xtel.vparking.presenter;

import android.app.Activity;

import com.xtel.vparking.view.activity.inf.IMapView;

/**
 * Created by Lê Công Long Vũ on 12/22/2016.
 */

public class MapPresenter {
    IMapView view;

    public MapPresenter(IMapView view) {
        this.view = view;
    }
}