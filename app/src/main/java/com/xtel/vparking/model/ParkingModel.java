package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.utils.SharedPreferencesUtils;

/**
 * Created by Lê Công Long Vũ on 12/4/2016.
 */

public class ParkingModel extends BasicModel {
    private static ParkingModel instanse = new ParkingModel();

    public static ParkingModel getInstanse() {
        return instanse;
    }

    private ParkingModel() {
    }

    public void getParkingInfo(String url, String session, ResponseHandle responseHandle) {
        requestServer.getApi(url, session, responseHandle);
    }

    public void getParkingByUser(String url, String session, ResponseHandle responseHandle) {
        requestServer.getApi(url, session, responseHandle);
    }

    public void getParkingAround(String url, ResponseHandle responseHandle) {
        requestServer.getApi(url, null, responseHandle);
    }

    public void addParking(String url, String json, String session, ResponseHandle responseHandle) {
        requestServer.postApi(url, json, session, responseHandle);
    }
}