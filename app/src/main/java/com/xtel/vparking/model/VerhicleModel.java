package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class VerhicleModel extends BasicModel {
    private static VerhicleModel instance = new VerhicleModel();

    public static VerhicleModel getInstance() {
        return instance;
    }

    public void getAllVerhicle(String url, String session, ResponseHandle responseHandle) {
        requestServer.getApi(url, session, responseHandle);
    }
}