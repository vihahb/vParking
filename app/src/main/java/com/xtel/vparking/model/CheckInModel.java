package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;

/**
 * Created by Lê Công Long Vũ on 12/14/2016.
 */

public class CheckInModel extends BasicModel {
    private static CheckInModel instance = new CheckInModel();

    public static CheckInModel getInstance() {
        return instance;
    }

    public void checkInVerhicle(String url, String jsonObject, String session, ResponseHandle responseHandle) {
        requestServer.postApi(url, jsonObject, session, responseHandle);
    }

    public void checkOutVerhicle(String url, String jsonObject, String session, ResponseHandle responseHandle) {
        requestServer.postApi(url, jsonObject, session, responseHandle);
    }
}
