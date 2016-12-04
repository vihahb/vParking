package com.xtel.vparking.presenter;

import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.view.activity.inf.HomeFragmentView;

/**
 * Created by Mr. M.2 on 12/4/2016.
 */

public class HomeFragmentPresenter {
    private HomeFragmentView view;

    public HomeFragmentPresenter(HomeFragmentView view) {
        this.view = view;
    }

    public void getParkingInfo(final int id) {
        ParkingModel.getInstanse().getParkingInfo(id, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onGetParkingInfoSuccuss(obj);
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
        GetNewSession.getNewSession(view.getFragmentActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingInfo(id);
            }

            @Override
            public void onError() {
                Toast.makeText(view.getFragmentActivity(), view.getFragmentActivity().getString(R.string.error_session_invalid), Toast.LENGTH_SHORT).show();
            }
        });
    }
}