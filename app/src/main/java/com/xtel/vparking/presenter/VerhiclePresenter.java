package com.xtel.vparking.presenter;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Verhicle;
import com.xtel.vparking.view.activity.inf.VerhicleView;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class VerhiclePresenter {
    private VerhicleView view;

    public VerhiclePresenter(VerhicleView view) {
        this.view = view;
    }

    public void getAllVerhicle() {
        String url = Constants.SERVER_PARKING + Constants.PARKING_VERHICLE;
        String session = LoginModel.getInstance().getSession();
        VerhicleModel.getInstance().getAllVerhicle(url, session, new ResponseHandle<RESP_Verhicle>(RESP_Verhicle.class) {
            @Override
            public void onSuccess(RESP_Verhicle obj) {
                view.onGetVerhicleSuccess(obj);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionVerhicle();
                else
                    view.onGetVerhicleError(error);
            }
        });
    }

    private void getNewSessionVerhicle() {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getAllVerhicle();
            }

            @Override
            public void onError() {
                view.onGetVerhicleError(new Error(2, "ERROR", "Bạn đã hết phiên làm việc"));
            }
        });
    }
}
