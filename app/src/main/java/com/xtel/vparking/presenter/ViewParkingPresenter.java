package com.xtel.vparking.presenter;

import android.util.Log;

import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.view.activity.inf.IParkingView;

/**
 * Created by Lê Công Long Vũ on 12/19/2016.
 */

public class ViewParkingPresenter {
    IParkingView view;

    public ViewParkingPresenter(IParkingView view) {
        this.view = view;
    }

    public void getData() {
        int id = -1;

        try {
            id = view.getActivity().getIntent().getIntExtra(Constants.ID_PARKING, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("vp", "id activity " + id);

        if (id == -1)
            view.onGetDataError();
        else
            view.onGetDataSuccess(id);
    }
}
