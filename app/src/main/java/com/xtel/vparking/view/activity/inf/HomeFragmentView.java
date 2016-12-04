package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking;
import com.xtel.vparking.model.entity.RESP_Parking_Info;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface HomeFragmentView {

    public void onGetParkingInfoSuccuss(RESP_Parking_Info resp_parking_info);
    public void onGetParkingInfoError(Error error);
    public void onGetParkingAroundSuccess(ArrayList<RESP_Parking> arrayList);
    public void onGetParkingAroundError(Error error);
    public Activity getFragmentActivity();
}
