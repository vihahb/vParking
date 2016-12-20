package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.CheckInHisObj;
import com.xtel.vparking.model.entity.Error;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/19/2016.
 */

public interface IViewHistory {
    void showShortToast(String message);

    void onNetworkDisable();

    void onGetHistorySuccess(ArrayList<CheckInHisObj> arrayList);

    void onGetHistoryError(Error error);

    void onItemClicked(CheckInHisObj checkInHisObj);

    void onEndlessScroll();

    Activity getActivity();
}
