package com.xtel.vparking.view.activity.inf;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.xtel.vparking.model.entity.Error;

import java.util.List;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public interface AddParkingView {

    void onTakePictureSuccess(Uri uri);
    void onPostPictureSuccess(String url);
    void onPostPictureError(String error);
    void onAddParkingSuccess(int id);
    void onAddParkingError(Error error);
    void onValidateError(View view, String error);
    Activity getActivity();
}