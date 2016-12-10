package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Verhicle;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public interface VerhicleView {

    void onGetVerhicleSuccess(RESP_Verhicle obj);
    void onGetVerhicleError(Error error);
    Activity getActivity();
}
