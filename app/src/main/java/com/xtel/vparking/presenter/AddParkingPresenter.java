package com.xtel.vparking.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Pictures;
import com.xtel.vparking.model.entity.PlaceModel;
import com.xtel.vparking.model.entity.Prices;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.Task;
import com.xtel.vparking.view.activity.inf.AddParkingView;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class AddParkingPresenter {
    private AddParkingView view;

    public AddParkingPresenter(AddParkingView view) {
        this.view = view;
    }

    public void takePicture(FragmentManager fragmentManager) {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(view.getActivity())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
                        Log.e("tb_uri", "uri: " + uri);
                        Log.e("tb_path", "uri.geta: " + uri.getPath());

                        view.onTakePictureSuccess(uri);
                    }
                })
                .setPeekHeight(view.getActivity().getResources().getDisplayMetrics().heightPixels / 2)
                .create();
        bottomSheetDialogFragment.show(fragmentManager);
    }

    public void postImage(Bitmap bitmap) {
        new Task.ConvertImage(view.getActivity(), true, new RequestWithStringListener() {
            @Override
            public void onSuccess(String url) {
                view.onPostPictureSuccess(url);
            }

            @Override
            public void onError() {
                view.onPostPictureError("Xảy ra lỗi. Vui lòng thử lại");
            }
        }).execute(bitmap);
    }

    public void validateData(View _view, ArrayList<Pictures> arrayList_picture, String parking_name, PlaceModel placeModel,
                             int transport_type, String total_place, String begin_time, String end_time,
                             ArrayList<Prices> arrayList_price) {

        if (arrayList_picture.size() == 0) {
            view.onValidateError(_view, view.getActivity().getString(R.string.loi_chonanh));
        } else if (parking_name.isEmpty()) {
            view.onValidateError(_view, view.getActivity().getString(R.string.loi_nhapten));
        } else if (placeModel == null) {
            view.onValidateError(_view, view.getActivity().getString(R.string.loi_vitri));
        } else if (transport_type == 0) {
            view.onValidateError(_view, view.getActivity().getString(R.string.error_choose_transport));
        } else if (checkNumberInput(total_place) <= 0) {
            view.onValidateError(_view, view.getActivity().getString(R.string.loi_chotrong));
        } else if (!checkListPrice(arrayList_price)) {
            view.onValidateError(_view, view.getActivity().getString(R.string.error_choose_money_price));
        } else {
            RESP_Parking_Info object = new RESP_Parking_Info();
            object.setLat(placeModel.getLatitude());
            object.setLng(placeModel.getLongtitude());
            object.setType(transport_type);
            object.setAddress(placeModel.getAddress());

            if (!begin_time.isEmpty())
                object.setBegin_time(begin_time);
            if (!end_time.isEmpty())
                object.setBegin_time(end_time);

            object.setTotal_place(total_place);
            object.setEmpty_number(total_place);
            object.setPrices(arrayList_price);
            object.setPictures(arrayList_picture);

            addParking(object);
        }
    }

    private boolean checkListPrice(ArrayList<Prices> arrayList_price) {
        for (int i = (arrayList_price.size() - 1); i >= 0; i--) {
            Log.e("tb_list", "item " + i + ":  " + arrayList_price.get(i).getPrice());
            if (arrayList_price.get(i).getPrice() == 0)
                return false;
        }
        return true;
    }

    private int checkNumberInput(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void addParking(final RESP_Parking_Info resp_parking_info) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING;
        String jsonObject = JsonHelper.toJson(resp_parking_info);
        String session = LoginModel.getInstance().getSession();

        Log.e("tb_session", session);
        Log.e("tb_url", url);
        Log.e("tb_json", jsonObject);

        ParkingModel.getInstanse().addParking(url, jsonObject, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onAddParkingSuccess(obj.getId());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSesstionAddParking(resp_parking_info);
                else
                    view.onAddParkingError(error);
            }
        });
    }

    private void getNewSesstionAddParking(final RESP_Parking_Info resp_parking_info) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                addParking(resp_parking_info);
            }

            @Override
            public void onError() {
                view.onAddParkingError(new Error(2, "", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }
}