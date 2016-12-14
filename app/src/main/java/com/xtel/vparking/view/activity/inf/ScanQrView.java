package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;

/**
 * Created by Mr. M.2 on 12/3/2016.
 */

public interface ScanQrView {

    void onSetupToolbar(String title);
    void onGetDataError();
    void onStartChecking();
    void onCheckingSuccess();
    void onCheckingError(Error error);
    Activity getActivity();
}
