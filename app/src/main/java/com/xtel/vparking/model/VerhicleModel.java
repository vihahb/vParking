package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.RESP_Brandname;
import com.xtel.vparking.model.entity.RESP_Verhicle;

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

    public void getCheckInByParkingId(int id, int page, int size, ResponseHandle responseHandle) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_CHECKIN_BY_PARKING_ID + id +
                Constants.PARKING_CHECKIN_PAGE + page + Constants.PARKING_CHECKIN_SIZE + size;
        String session = LoginModel.getInstance().getSession();
        requestServer.getApi(url, session, responseHandle);
    }

    public void getVerhicleById(String url, String session, ResponseHandle responseHandle) {
        requestServer.getApi(url, session, responseHandle);
    }

    public void getBrandName(String url, ResponseHandle<RESP_Brandname> responseHandle) {
        requestServer.getApi(url, null, responseHandle);
    }

    public void addVerhicle2Nip(String url, String object, String session, ResponseHandle responseHandle) {
        requestServer.postApi(url, object, session, responseHandle);
    }

    public void putVerhicle2Server(String url, String object, String session, ResponseHandle responseHandle) {
        requestServer.putApi(url, object, session, responseHandle);
    }

}
