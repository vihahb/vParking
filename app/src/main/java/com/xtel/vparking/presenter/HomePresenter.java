package com.xtel.vparking.presenter;

import android.util.Log;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.HomeModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.MyApplication;
import com.xtel.vparking.view.activity.inf.HomeView;

import java.io.UnsupportedEncodingException;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class HomePresenter {
    private HomeView homeView;

    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
        checkGps();
        checkParkingMaster();
    }

    private void checkGps() {
        NetWorkInfo.checkGPS(homeView.getActivity());
    }

    private void checkParkingMaster() {
        if (SharedPreferencesUtils.getInstance().getIntValue(Constants.USER_FLAG) == 1)
            homeView.isParkingMaster();
    }

    public void updateUserData() {
        String avatar = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_AVATAR);
        String full_name = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_FULL_NAME);

        homeView.onUserDataUpdate(avatar, full_name);
    }

    public void activeParkingMaster() {
        homeView.showShortToast("get parking");

        try {
            String url = Constants.SERVER_PARKING + Constants.PARKING_ACTIVE;
            HomeModel.getInstance().activeParkingMaster(url, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
                @Override
                public void onSuccess(RESP_Parking_Info obj) {
                    Log.e(homeView.getActivity().getClass().getSimpleName(), "null k: " + obj.toString());
                    SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_FLAG, 1);
                    homeView.onActiveMasterSuccess();
                }

                @Override
                public void onError(Error error) {
                    if (error.getCode() == 2)
                        getNewSessionActive();
                    else
                        homeView.onActiveMasterFailed(JsonParse.getCodeMessage(error.getCode(), homeView.getActivity().getString(R.string.loi_coloi)));
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getNewSessionActive() {
        GetNewSession.getNewSession(MyApplication.context, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                activeParkingMaster();
            }

            @Override
            public void onError() {
                homeView.showShortToast(homeView.getActivity().getString(R.string.error_session_invalid));
//                Toast.makeText(homeView.getActivity(), homeView.getActivity().getString(R.string.error_session_invalid), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
