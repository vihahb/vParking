package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.xtel.vparking.model.entity.PlaceModel;

/**
 * Created by Lê Công Long Vũ on 12/23/2016.
 */

public interface IChooseMapsView {

    void onGetData(PlaceModel placeModel);
    void onGetAddressSucces(double lat, double lng, String address);
    void onGetAddressError(double lat, double lng);
    void onGetMyLocation(LatLng latLng);
    Activity getActivity();
}
