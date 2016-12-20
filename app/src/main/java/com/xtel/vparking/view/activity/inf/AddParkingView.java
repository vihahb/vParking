package com.xtel.vparking.view.activity.inf;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingInfo;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public interface AddParkingView {

    void showShortToast(String message);
    void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message);
    void onGetDataSuccess(ParkingInfo object);
    void onTakePictureSuccess(Uri uri);
    void onPostPictureSuccess(String url);
    void onAddPictureSuccess(String url);
    void onAddPictureError(Error error);
    void onDeletePictureSuccess();
    void onDeletePictureError(Error error);
    void onDeletePriceSuccess(int position);
    void onDeletePriceError(Error error);
    void onPostPictureError(String error);
    void onGetTimeSuccess(boolean isBegin, String hour, String minute);
    void onAddParkingSuccess(int id);
    void onUpdateParkingSuccess(ParkingInfo parkingInfo);
    void onAddParkingError(Error error);
    void onUpdateParkingError(Error error);
    void onValidateError(View view, String error);
    Activity getActivity();
}