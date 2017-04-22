package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.CheckInVerhicle;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Verhicle;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public interface CheckInView extends BasicView {

    void showShortToast(String message);
    void onNetworkDisable();
    void onGetVerhicleSuccess(ArrayList<Verhicle> arrayList);
    void onGetVerhicleError(Error error);
    void onItemClicked(CheckInVerhicle checkInVerhicle);
    Activity getActivity();
}
