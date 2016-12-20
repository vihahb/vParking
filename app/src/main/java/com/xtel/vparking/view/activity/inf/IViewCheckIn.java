package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingCheckIn;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface IViewCheckIn {

    void showShortToast(String message);
    void onNetworkDisable();
    void onGetVerhicleSuccess(ArrayList<ParkingCheckIn> arrayList);
    void onGetVerhicleError(Error error);
    void onEndlessScroll();
    void onItemClicked(ParkingCheckIn checkIn);
    Activity getActivity();
}
