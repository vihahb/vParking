package com.xtel.vparking.presenter;

import android.content.Intent;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.FindParking;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Find;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.inf.FindAdvancedView;

/**
 * Created by vivhp on 12/9/2016.
 */

public class FindAdvancedPresenter {
    private FindAdvancedView view;

    public FindAdvancedPresenter(FindAdvancedView view) {
        this.view = view;
    }

    public void getParkingRequest(int type, int price, int price_type, String begin_time, String end_time){
        String url = Constants.SERVER_PARKING + Constants.PARKING_FIND
                + "&type=" + type
                + "&price=" + price
                + "&price_type=" + price_type
                + "&begin_time=" + begin_time
                + "&end_time=" + end_time;
        Find find = new Find();
        find.setType(type);
        find.setPrice(price);
        find.setPrice_type(price_type);
        find.setBegin_time(begin_time);
        find.setEnd_time(end_time);
        view.putExtras(Constants.FIND_MODEL, find);
    }
}
