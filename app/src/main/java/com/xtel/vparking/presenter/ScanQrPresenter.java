package com.xtel.vparking.presenter;

import android.os.Handler;

import com.xtel.vparking.view.activity.inf.ScanQrView;

/**
 * Created by Mr. M.2 on 12/3/2016.
 */

public class ScanQrPresenter {
    private ScanQrView view;

    public ScanQrPresenter(ScanQrView scanQrView) {
        this.view = scanQrView;
    }

    public void showResult(final String title, final String content) {
        view.startScanQrCode();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.endScanQrCode(title, content);
            }
        }, 2000);
    }
}