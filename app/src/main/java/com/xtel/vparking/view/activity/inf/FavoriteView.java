package com.xtel.vparking.view.activity.inf;

import android.app.Activity;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Favotire;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/5/2016.
 */

public interface FavoriteView extends BasicView {

    void onNetworkDisable();
    void onRemoveItemSuccess();
    void showShortToast(String message);
    void showLongToast(String message);
    void onGetParkingFavoriteSuccess(ArrayList<Favotire> arrayList);
    void onGetParkingFavoriteError(Error error);
    Activity getActivity();
}
