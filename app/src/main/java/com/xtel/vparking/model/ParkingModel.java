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

    public void getParkingAround(double lat, double lng, int prices, int type, String begin_time, String end_time, ResponseHandle responseHandle) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_FIND +
                Constants.PARKING_LAT + lat +
                Constants.PARKING_LNG + lng;
        if (prices != -1)
            url += Constants.PARKING_PRICE + prices;
        if (type != -1)
            url += Constants.PARKING_TYPE + type;
        if (begin_time != null)
            url += Constants.PARKING_BEGIN_TIME + begin_time;
        if (end_time != null)
            url += Constants.PARKING_END_TIME + end_time;

        requestServer.getApi(url, null, responseHandle);
    }

    public void addParking(String url, String json, String session, ResponseHandle responseHandle) {
        requestServer.postApi(url, json, session, responseHandle);
    }
}