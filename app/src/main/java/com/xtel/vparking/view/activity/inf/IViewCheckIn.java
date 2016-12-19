package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface IViewCheckIn {

    void showShortToast(String message);
    void onNetworkDisable();
    void onGetVerhicleSuccess(ArrayList<CheckIn> arrayList);
    void onGetVerhicleError(Error error);
    void onItemClicked(CheckIn checkIn);
    Activity getActivity();
}
