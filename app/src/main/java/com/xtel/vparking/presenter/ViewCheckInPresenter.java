package com.xtel.vparking.presenter;

import android.os.Handler;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Check_In;
import com.xtel.vparking.view.activity.inf.CheckedView;
import com.xtel.vparking.view.activity.inf.IViewCheckIn;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class ViewCheckInPresenter {
    private IViewCheckIn view;
    private boolean isViewing = true;
    private int id = -1, page = 1, size = 20;

    public ViewCheckInPresenter(IViewCheckIn view, int id) {
        this.view = view;
        this.id = id;
    }

    public void getCheckIn() {
        if (!NetWorkInfo.isOnline(view.getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.onNetworkDisable();
                }
            }, 500);

            return;
        }

        VerhicleModel.getInstance().getCheckInByParkingId(id, page, size, new ResponseHandle<RESP_Check_In>(RESP_Check_In.class) {
            @Override
            public void onSuccess(RESP_Check_In obj) {
                if (isViewing) {
                    page++;
                    size += 20;
                    view.onGetVerhicleSuccess(obj.getData());
                }
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionVerhicle();
                else if (isViewing)
                    view.onGetVerhicleError(error);
            }
        });
    }

    private void getNewSessionVerhicle() {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                if (isViewing)
                    getCheckIn();
            }

            @Override
            public void onError() {
                if (isViewing)
                    view.onGetVerhicleError(new Error(2, "ERROR", "Bạn đã hết phiên làm việc"));
            }
        });
    }

    public void destroyView() {
        isViewing = false;
    }
}