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
import com.xtel.vparking.view.activity.inf.CheckedView;
import com.xtel.vparking.model.entity.RESP_Check_In;

/**
 * Created by Lê Công Long Vũ on 12/2/2016
 */

public class CheckedPresenter {
    private CheckedView view;
    private boolean isViewing = true;

    public CheckedPresenter(CheckedView view) {
        this.view = view;
    }

    public void getAllVerhicle() {
        if (!NetWorkInfo.isOnline(view.getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.onNetworkDisable();
                }
            }, 500);

            return;
        }

        VerhicleModel.getInstance().getAllVerhicleByUser(new ResponseHandle<RESP_Check_In>(RESP_Check_In.class) {
            @Override
            public void onSuccess(RESP_Check_In obj) {
                if (isViewing)
                    view.onGetVerhicleSuccess(obj.getData());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionVerhicle();
                else if (isViewing)
                    view.onGetVerhicleError(error);
            }

            @Override
            public void onUpdate() {
                view.onUpdateVersion();
            }
        });
    }

    private void getNewSessionVerhicle() {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                if (isViewing)
                    getAllVerhicle();
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