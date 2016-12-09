package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.model.entity.RESP_Brandname;

/**
 * Created by vivhp on 12/9/2016.
 */

public class VerhicleModel extends BasicModel {
    public static VerhicleModel instance = new VerhicleModel();

    public static VerhicleModel getInstance() {
        return instance;
    }

    public void getBrandName(String url, ResponseHandle<RESP_Brandname> responseHandle) {
        requestServer.getApi(url, null, responseHandle);
    }

}
