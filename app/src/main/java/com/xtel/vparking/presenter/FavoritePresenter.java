package com.xtel.vparking.presenter;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Favorite;
import com.xtel.vparking.model.entity.RESP_Parking_Info_List;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.inf.FavoriteView;

/**
 * Created by Lê Công Long Vũ on 12/5/2016.
 */

public class FavoritePresenter {
    private FavoriteView view;

    public FavoritePresenter(FavoriteView view) {
        this.view = view;
    }

    public void getParkingFavorite() {
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        String url = Constants.SERVER_PARKING + Constants.PARKING_GET_FAVORITE;
        ParkingModel.getInstanse().getParkingByUser(url, session, new ResponseHandle<RESP_Favorite>(RESP_Favorite.class) {
            @Override
            public void onSuccess(RESP_Favorite obj) {
                view.onGetParkingFavoriteSuccess(obj.getData());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionAddParking();
                else
                    view.onGetParkingFavoriteError(error);
            }
        });
    }

    private void getNewSessionAddParking() {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingFavorite();
            }

            @Override
            public void onError() {
                view.onGetParkingFavoriteError(new Error(2, "", "Đã xảy ra lỗi"));
            }
        });
    }
}
