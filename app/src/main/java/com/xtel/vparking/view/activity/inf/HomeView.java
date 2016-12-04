package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public interface HomeView {

    public void isParkingMaster();
    public void onActiveMasterSuccess();
    public void onActiveMasterFailed(String error);
    public void onUserDataUpdate(String avatar, String name);
    public Activity getActivity();
}
