package com.xtel.vparking.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.model.entity.RESP_Parking_Info_List;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.utils.Task;
import com.xtel.vparking.view.activity.inf.AddParkingView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class AddParkingPresenter {
    private AddParkingView view;

    public AddParkingPresenter(AddParkingView view) {
        this.view = view;
    }

    public void postImage(Bitmap bitmap) {
        new Task.ConvertImage(view.getActivity(), true, new RequestWithStringListener() {
            @Override
            public void onSuccess(String url) {
                view.onTakePictureSucces(url);
            }

            @Override
            public void onError() {
                view.onTakePictureError();
            }
        }).execute(bitmap);
    }

    public void postImage() {

    }

    public void addParking(final double lat, final double lng, final int type, final String address, final String begin_time, final String end_time,
                           final String parking_name, final String desc, final int total_place, final int prices, final ArrayList<String> arrayList_file) {
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        JsonObject json = new JsonObject();
        json.addProperty(Constants.JSON_LAT, lat);
        json.addProperty(Constants.JSON_LNG, lng);
        json.addProperty(Constants.JSON_TYPE, type);
        json.addProperty(Constants.JSON_ADDRESS, address);

        if (!begin_time.isEmpty())
            json.addProperty(Constants.JSON_BEGIN_TIME, begin_time);
        if (!end_time.isEmpty())
            json.addProperty(Constants.JSON_END_TIME, end_time);

        json.addProperty(Constants.JSON_PARKING_NAME, parking_name);

        if (!desc.isEmpty())
            json.addProperty(Constants.JSON_PARKING_DESC, "null");

        if (total_place != -1) {
            json.addProperty(Constants.JSON_TOTAL_PLACE, total_place);
            json.addProperty(Constants.JSON_EMPTY_NUMBER, total_place);
        }

//        Add Prices
        JsonArray all_prices = new JsonArray();
        JsonObject price = new JsonObject();
        price.addProperty(Constants.JSON_NAME, "");
        price.addProperty(Constants.JSON_PRICE, (prices * 5));
        price.addProperty(Constants.JSON_PRICE_TYPE, 1);
        all_prices.add(price);
        json.add(Constants.JSON_PRICES, all_prices);

        JsonArray all_picture = new JsonArray();
        for (int i = 0; i < arrayList_file.size(); i++) {
            JsonObject picture = new JsonObject();
            picture.addProperty(Constants.JSON_URL, arrayList_file.get(i));
            all_picture.add(picture);
        }
        json.add(Constants.JSON_PICTURES, all_picture);

        Log.e("tb_session", session);
        Log.e("tb_url", Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING);
        Log.e("tb_json", json.toString());

        String url = Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING;

        ParkingModel.getInstanse().addParking(url, json.toString(), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onAddParkingSuccess(obj.getId());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSesstionAddParking(lat, lng, type, address, begin_time, end_time, parking_name, desc, total_place, prices, arrayList_file);
                else
                    view.onAddParkingError(error);
            }
        });
    }

    private void getNewSesstionAddParking(final double lat, final double lng, final int type, final String address, final String begin_time, final String end_time,
                                          final String parking_name, final String desc, final int total_place, final int prices, final ArrayList<String> arrayList_file) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                addParking(lat, lng, type, address, begin_time, end_time, parking_name, desc, total_place, prices, arrayList_file);
            }

            @Override
            public void onError() {
                view.onAddParkingError(new Error(2, "", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }
}