package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.model.entity.RESP_Parking_Info;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/5/2016.
 */

public interface ManagementView extends BasicView {

    void onNetworkDisable();
    void onGetParkingByUserSuccess(ArrayList<ParkingInfo> arrayList);
    void onGetParkingByUserError(Error error);
    void onGetParkingInfoSuccess(ParkingInfo parkingInfo);
    Activity getActivity();
}