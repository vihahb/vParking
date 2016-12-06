package com.xtel.vparking.presenter;

import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.model.entity.RESP_Parking_Info_List;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.inf.ManagementView;

/**
 * Created by Mr. M.2 on 12/5/2016.
 */

public class ManagementPresenter {
    private ManagementView view;

    public ManagementPresenter(ManagementView view) {
        this.view = view;
    }

    public void getParkingByUser() {
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        String url = Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING;
        ParkingModel.getInstanse().getParkingByUser(url, session, new ResponseHandle<RESP_Parking_Info_List>(RESP_Parking_Info_List.class) {
            @Override
            public void onSuccess(RESP_Parking_Info_List obj) {
                view.onGetParkingByUserSuccess(obj.getData());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionAddParking();
                else
                    view.onGetParkingByUserError(error);
            }
        });
    }

    private void getNewSessionAddParking() {
        GetNewSession.getNewSession(view.getFragmentActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingByUser();
            }

            @Override
            public void onError() {
                view.onGetParkingByUserError(new Error(2, "", "Đã xảy ra lỗi"));
            }
        });
    }

    public void getParkingInfo(final int id) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + id;
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);

        ParkingModel.getInstanse().getParkingInfo(url, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                ParkingInfo parkingInfo = new ParkingInfo();
                parkingInfo.setId(obj.getId());
                parkingInfo.setUid(obj.getUid());
                parkingInfo.setLat(obj.getLat());
                parkingInfo.setLng(obj.getLng());
                parkingInfo.setType(obj.getType());
                parkingInfo.setStatus(obj.getStatus());
                parkingInfo.setCode(obj.getCode());
                parkingInfo.setBegin_time(obj.getBegin_time());
                parkingInfo.setEnd_time(obj.getEnd_time());
                parkingInfo.setAddress(obj.getAddress());
                parkingInfo.setParking_name(obj.getParking_name());
                parkingInfo.setParking_desc(obj.getParking_desc());
                parkingInfo.setTotal_place(obj.getTotal_place());
                parkingInfo.setEmpty_number(obj.getEmpty_number());
                parkingInfo.setQr_code(obj.getQr_code());
                parkingInfo.setBar_code(obj.getBar_code());
                parkingInfo.setPrices(obj.getPrices());
                parkingInfo.setFavorite(obj.getFavorite());

                view.onGetParkingInfoSuccess(parkingInfo);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionParkingInfo(id);
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
            public void onError() {}
        });
    }

}
