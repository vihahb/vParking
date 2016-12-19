package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface HomeView extends IView {

    void isParkingMaster();
    void onActiveMasterSuccess();
    void onActiveMasterFailed(String error);
    void onUserDataUpdate(String avatar, String name);
    void onShowQrCode(String url);
    void onViewParkingSelected(int id);
    Activity getActivity();
}
