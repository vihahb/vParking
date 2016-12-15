package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Verhicle;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public interface VerhicleView {

    void onNetworkDisable();
    void onGetVerhicleSuccess(ArrayList<Verhicle> arrayList);
    void onGetVerhicleError(Error error);
    void onGetVerhicleByIdSuccess(Verhicle verhicle);
    void onItemClicked(int position, Verhicle verhicle);
    Activity getActivity();
}
