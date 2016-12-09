package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

/**
 * Created by vivhp on 12/9/2016.
 */

public interface AddVerhicleView extends IView {

    void onAddVerhicleSuccess();

    void onAddVerhicleError();


    Activity getActivity();

}
