package com.xtel.vparking.presenter;

import android.os.Handler;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Favorite;
import com.xtel.vparking.view.activity.inf.FavoriteView;

/**
 * Created by Lê Công Long Vũ on 12/5/2016.
 */

public class FavoritePresenter {
    private FavoriteView view;
    private boolean isViewing = true;

    public FavoritePresenter(FavoriteView view) {
        this.view = view;
    }

    public void getParkingFavorite() {
        if (!NetWorkInfo.isOnline(view.getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.onNetworkDisable();
                }
            }, 500);

            return;
        }

        String session = LoginModel.getInstance().getSession();
        String url = Constants.SERVER_PARKING + Constants.PARKING_GET_FAVORITE;
        ParkingModel.getInstanse().getParkingByUser(url, session, new ResponseHandle<RESP_Favorite>(RESP_Favorite.class) {
            @Override
            public void onSuccess(RESP_Favorite obj) {
                if (isViewing)
                    view.onGetParkingFavoriteSuccess(obj.getData());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionAddParking();
                else if (isViewing)
                    view.onGetParkingFavoriteError(error);
            }

            @Override
            public void onUpdate() {
                view.onUpdateVersion();
            }
        });
    }

    private void getNewSessionAddParking() {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                if (isViewing)
                    getParkingFavorite();
            }

            @Override
            public void onError() {
                if (isViewing)
                    view.onGetParkingFavoriteError(new Error(2, "", "Đã xảy ra lỗi"));
            }
        });
    }

    public void destroyView() {
        isViewing = false;
    }
}
