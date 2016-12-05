package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

/**
 * Created by Mr. M.2 on 12/3/2016.
 */

public interface ScanQrView {

    public void onSetupToolbar(String title);
    public void startScanQrCode();
    public void endScanQrCode(String title, String content);
    public Activity getActivity();
}
