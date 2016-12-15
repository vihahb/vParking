package com.xtel.vparking.presenter;

import android.util.Log;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.CheckInModel;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.view.activity.inf.CheckOutView;
import com.xtel.vparking.view.fragment.CheckedFragment;

/**
 * Created by Lê Công Long Vũ on 12/14/2016.
 */

public class CheckOutPresenter {
    CheckOutView view;
    private CheckIn checkIn;

    public CheckOutPresenter(CheckOutView view) {
        this.view = view;
    }

    public void getData() {
        try {
            checkIn = (CheckIn) view.getActivity().getIntent().getSerializableExtra(CheckedFragment.CHECKED_OBJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkIn != null) {
            view.onGetDataSuccess(checkIn);
            getParkingInfo(checkIn.getParking().getId());
        }
        else
            view.onGetDataError();
    }

    private void getParkingInfo(final int id) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + id;
        String session = LoginModel.getInstance().getSession();

        ParkingModel.getInstanse().getParkingInfo(url, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                Log.e(this.getClass().getSimpleName(), "parking info " + JsonHelper.toJson(obj));
                view.onGetParkingInfoSuccess(obj);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionParkingInfo(id);
                else
                    view.onGetParkingInfoError(error);
            }
        });
    }

    private void getNewSessionParkingInfo(final int id) {
        Log.e("home", "old: " + LoginModel.getInstance().getSession());
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingInfo(id);
            }

            @Override
            public void onError() {
                view.onGetParkingInfoError(new Error(2, "ERROR", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }

    public void viewParking() {
        view.onViewParking(checkIn.getParking().getId());
    }

    public void checkOut() {
        String url = Constants.SERVER_PARKING + Constants.PARKING_CHECK_OUT;
        String session = LoginModel.getInstance().getSession();
//        String jsonObjec = "{transaction:\"" + checkIn.getTransaction() + "\"}";
//        Log.e(this.getClass().getSimpleName(), "json object " + jsonObjec);

        CheckInModel.getInstance().checkOutVerhicle(url, JsonHelper.toJson(checkIn), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onCheckOutSuccess(checkIn);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionCheckOut();
                else
                    view.onCheckOutError(error);
            }
        });
    }

    private void getNewSessionCheckOut() {
        Log.e("home", "old: " + LoginModel.getInstance().getSession());
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                checkOut();
            }

            @Override
            public void onError() {
                view.onCheckOutError(new Error(2, "ERROR", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }
}