package com.xtel.vparking.presenter;

import android.util.Log;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.CheckInModel;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.entity.CheckInVerhicle;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.view.activity.CheckInActivity;
import com.xtel.vparking.view.activity.inf.ScanQrView;

/**
 * Created by Lê Công Long Vũ on 12/3/2016.
 */

public class ScanQrPresenter {
    private ScanQrView view;
    private CheckInVerhicle checkInVerhicle;

    public ScanQrPresenter(ScanQrView scanQrView) {
        this.view = scanQrView;
    }

    public void getData() {
        try {
            checkInVerhicle = (CheckInVerhicle) view.getActivity().getIntent().getSerializableExtra(CheckInActivity.CHECK_IN_OBJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkInVerhicle != null) {
            checkTransportScan();
        } else {
            view.onGetDataError();
        }
    }

    private void checkTransportScan() {
        switch (checkInVerhicle.getCheckin_type()) {
            case 1:
                view.onSetupToolbar("Ô tô");
                break;
            case 2:
                view.onSetupToolbar("Xe máy");
                break;
            case 3:
                view.onSetupToolbar("Xe đạp");
                break;
            default:
                view.onSetupToolbar("Check In");
                break;
        }
    }

    public void startCheckIn(String gift_code, String content) {
        view.onStartChecking();
        String url = Constants.SERVER_PARKING + Constants.PARKING_CHECK_IN;
        String session = LoginModel.getInstance().getSession();
        checkInVerhicle.setParking_code(content);

        Log.e(this.getClass().getSimpleName(), "json " + JsonHelper.toJson(checkInVerhicle) + "  " + checkInVerhicle.getCheckin_type() + "   "
                + checkInVerhicle.getParking_code() + "  " + checkInVerhicle.getVerhicle_id());

        CheckInModel.getInstance().checkInVerhicle(url, JsonHelper.toJson(checkInVerhicle), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onCheckingSuccess();
            }

            @Override
            public void onError(Error error) {
                view.onCheckingError(error);
            }
        });
    }
}