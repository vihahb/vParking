package com.xtel.vparking.presenter;

import android.util.Log;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Brandname;
import com.xtel.vparking.view.activity.inf.AddVerhicleView;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/9/2016.
 */

public class AddVerhiclePresenter {
    public AddVerhicleView view;

    public AddVerhiclePresenter(AddVerhicleView view) {
        this.view = view;
    }

    public void getVerhicleBrandname() {
        int version = 0;
        String url_brand_name = Constants.SERVER_PARKING
                + Constants.GET_BRANDNAME
                + Constants.BRAND_NAME
                + Constants.BRANDNAME_VERSION
                + version;

        VerhicleModel.getInstance().getBrandName(url_brand_name, new ResponseHandle<RESP_Brandname>(RESP_Brandname.class) {
            @Override
            public void onSuccess(RESP_Brandname obj) {
                Log.v("brandname", obj.toString());
                view.onGetBrandnameData(obj.getData());
            }

            @Override
            public void onError(Error error) {
                Log.e("brandname err code", String.valueOf(error.getCode()));
                Log.e("brandname err mess", error.getMessage());
            }
        });

    }
}
