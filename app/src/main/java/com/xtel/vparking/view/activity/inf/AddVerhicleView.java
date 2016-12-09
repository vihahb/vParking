package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.VerhicleBrandName;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/9/2016.
 */

public interface AddVerhicleView extends IView {

    void onAddVerhicleSuccess();

    void onAddVerhicleError();

    void onGetBrandnameData(ArrayList<VerhicleBrandName> brandNames);

    Activity getActivity();

}
