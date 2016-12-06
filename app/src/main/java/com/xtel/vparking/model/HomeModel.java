package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.utils.SharedPreferencesUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public class HomeModel extends BasicModel {
    private static final HomeModel instance = new HomeModel();

    public static HomeModel getInstance() {
        return instance;
    }

    public void activeParkingMaster(String url, ResponseHandle responseHandle) throws UnsupportedEncodingException {
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        requestServer.postApi(url, "", session, responseHandle);
    }
}
