package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import java.util.List;

import vn.xtel.quanlybaido.model.ErrorModel;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface AddParkingView {

    public void getListParkingSuccess(List<Object> list);
    public void getListParkingError(ErrorModel errorModel);
    public void getNewParking(Object object);
    public Activity getActivity();
}
