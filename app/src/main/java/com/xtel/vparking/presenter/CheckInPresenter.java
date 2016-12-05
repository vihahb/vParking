package com.xtel.vparking.presenter;

import android.content.Intent;

import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.view.activity.ScanQrActivity;
import com.xtel.vparking.view.activity.inf.CheckInView;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class CheckInPresenter {
    private CheckInView view;

    public CheckInPresenter(CheckInView checkInView) {
        this.view = checkInView;
    }

    public void startScanQrCode(int type, int requestCode) {
        Intent intent = new Intent(view.getActivity(), ScanQrActivity.class);
        intent.putExtra(Constants.TYPE_OF_TRANSPORT, type);
        view.getActivity().startActivityForResult(intent, requestCode);
//        startActivityForResultWithInteger(ScanQrActivity.class, , type, REQUEST_CODE);
    }

    public void checkScanResult(Intent data) {
        String content = data.getStringExtra(Constants.SCAN_RESULT);

        if (content != null)
            view.onScanSuccess(content);
    }
}