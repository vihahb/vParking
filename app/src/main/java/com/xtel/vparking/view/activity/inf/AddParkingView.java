package com.xtel.vparking.view.activity.inf;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;

import java.util.List;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public interface AddParkingView {

    void showShortToast(String message);
    void onGetDataSuccess(RESP_Parking_Info object);
    void onTakePictureSuccess(Uri uri);
    void onPostPictureSuccess(String url);
    void onPostPictureError(String error);
    void onGetTimeSuccess(boolean isBegin, String hour, String minute);
    void onAddParkingSuccess(int id);
    void onAddParkingError(Error error);
    void onValidateError(View view, String error);
    Activity getActivity();
}