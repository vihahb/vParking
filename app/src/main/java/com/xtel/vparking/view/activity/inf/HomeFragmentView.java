package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Parking;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.model.entity.RESP_Parking;
import com.xtel.vparking.model.entity.RESP_Parking_Info;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface HomeFragmentView extends IView {

    void onGetParkingInfoSuccuss(RESP_Parking_Info resp_parking_info);
    void onGetParkingInfoError(Error error);
    void onSearchParking(int id);
    void onGetParkingAroundSuccess(ArrayList<Parking> arrayList);
    void onGetParkingAroundError(Error error);
    Activity getActivity();
}
