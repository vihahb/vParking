package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;

import java.util.List;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface AddParkingView {

    public void getListParkingSuccess(List<Object> list);
    public void getListParkingError(Error error);
    public void getNewParking(Object object);
    public Activity getActivity();
}
