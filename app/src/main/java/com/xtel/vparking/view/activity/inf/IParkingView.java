package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

/**
 * Created by Lê Công Long Vũ on 12/19/2016.
 */

public interface IParkingView extends BasicView {

    void onGetDataSuccess(int id);
    void onGetDataError();
    Activity getActivity();
}
