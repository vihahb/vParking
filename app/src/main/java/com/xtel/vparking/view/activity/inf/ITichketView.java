package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;

/**
 * Created by Lê Công Long Vũ on 12/14/2016.
 */

public interface ITichketView {

    void onGetDataSuccess(String name, String time, String plate_number, String ticket_code);
    void onGetDataError();
    void onGetParkingInfoSuccess(RESP_Parking_Info obj);
    void onGetParkingInfoError(Error error);
    void onViewParking(int id);
    void onCheckOutSuccess(CheckIn checkIn);
    void onCheckOutError(Error error);
    void hideButton();
    Activity getActivity();
}