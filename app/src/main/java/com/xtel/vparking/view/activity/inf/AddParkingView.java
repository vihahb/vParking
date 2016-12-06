package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;

import java.util.List;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public interface AddParkingView {

    public void onTakePictureSucces(String url);
    public void onTakePictureError();
    public void onAddParkingSuccess(int id);
    public void onAddParkingError(Error error);
    public Activity getActivity();
}