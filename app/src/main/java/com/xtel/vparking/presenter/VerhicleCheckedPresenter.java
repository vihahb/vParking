package com.xtel.vparking.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Verhicle_List;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.activity.inf.RESP_Check_In;
import com.xtel.vparking.view.activity.inf.VerhicleCheckedView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class VerhicleCheckedPresenter {
    private VerhicleCheckedView view;

    public VerhicleCheckedPresenter(VerhicleCheckedView view) {
        this.view = view;
    }

    public void getAllVerhicle() {
        String url = Constants.SERVER_PARKING + Constants.PARKING_GET_CHECK_IN_BY_USER;
        String session = LoginModel.getInstance().getSession();
        VerhicleModel.getInstance().getAllVerhicle(url, session, new ResponseHandle<RESP_Check_In>(RESP_Check_In.class) {
            @Override
            public void onSuccess(RESP_Check_In obj) {
                view.onGetVerhicleSuccess(obj.getData());
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
        Log.e("verhicle", "get new session");
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                Log.e("verhicle", "get new session success");
                getAllVerhicle();
            }

            @Override
            public void onError() {
                Log.e("verhicle", "get new session error");
                view.onGetVerhicleError(new Error(2, "ERROR", "Bạn đã hết phiên làm việc"));
            }
        });
    }
}