package com.xtel.vparking.presenter;

import android.os.Handler;

import com.xtel.vparking.commons.Constants;
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

    public void checkTransportScan() {
        int type = view.getActivity().getIntent().getIntExtra(Constants.TYPE_OF_TRANSPORT, -1);

        switch (type) {
            case 2:
                view.onSetupToolbar("Ô tô");
                break;
            case 3:
                view.onSetupToolbar("Xe máy");
                break;
            case 4:
                view.onSetupToolbar("Xe tải");
                break;
            case 5:
                view.onSetupToolbar("Xe khách");
                break;
            default:
                view.onSetupToolbar(null);
                break;
        }
    }
}