package com.xtel.vparking.presenter;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.inf.HomeFragmentView;

/**
 * Created by Mr. M.2 on 12/4/2016.
 */

public class HomeFragmentPresenter {
    private HomeFragmentView view;

    public HomeFragmentPresenter(HomeFragmentView view) {
        this.view = view;
    }

    public void checkResultSearch() {
        if (HomeActivity.PARKING_ID != -1) {
            view.onSearchParking(HomeActivity.PARKING_ID);
            getParkingInfo(HomeActivity.PARKING_ID);
        }

        HomeActivity.PARKING_ID = -1;
    }

    public void getParkingInfo(final int id) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + id;
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);

        ParkingModel.getInstanse().getParkingInfo(url, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
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
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingInfo(id);
            }

            @Override
            public void onError() {
                view.showShortToast(view.getActivity().getString(R.string.error_session_invalid));
            }
        });
    }

    public void getParkingAround(double lat, double lng, int prices, int type, String begin_time, String end_time) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_FIND + Constants.PARKING_LAT + lat + Constants.PARKING_LNG + lng;
        if (prices != -1)
            url += Constants.PARKING_PRICE + prices;
        if (type != -1)
            url += Constants.PARKING_TYPE + type;
        if (begin_time != null)
            url += Constants.PARKING_BEGIN_TIME + begin_time;
        if (end_time != null)
            url += Constants.PARKING_END_TIME + end_time;

        ParkingModel.getInstanse().getParkingAround(url, new ResponseHandle<RESP_Parking>(RESP_Parking.class) {
            @Override
            public void onSuccess(RESP_Parking obj) {
                view.onGetParkingAroundSuccess(obj.getData());
            }

            @Override
            public void onError(Error error) {
                view.onGetParkingAroundError(error);
            }
        });
    }
}