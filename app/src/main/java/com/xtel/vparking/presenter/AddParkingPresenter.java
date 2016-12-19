package com.xtel.vparking.presenter;

import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.ICmd;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.model.entity.Pictures;
import com.xtel.vparking.model.entity.PlaceModel;
import com.xtel.vparking.model.entity.Prices;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.Task;
import com.xtel.vparking.view.activity.inf.AddParkingView;

import java.util.ArrayList;
import java.util.Calendar;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class AddParkingPresenter extends BasicPresenter {
    private AddParkingView view;
    private ParkingInfo object;
    private int picture_id = -1;

    private ICmd cmd = new ICmd() {
        @Override
        public void execute(Object... params) {
            ParkingModel.getInstanse().deleteParkingPicrute(picture_id, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
                @Override
                public void onSuccess(RESP_Parking_Info obj) {
                    view.onDeletePictureSuccess();
                }

                @Override
                public void onError(Error error) {
                    if (error.getCode() == 2)
                        getNewSession(cmd);
                    else
                        view.onDeletePictureError(error);
                }
            });
        }
    };

    public AddParkingPresenter(AddParkingView view) {
        this.view = view;
    }

    public void getData() {
        try {
            object = (ParkingInfo) view.getActivity().getIntent().getSerializableExtra(Constants.PARKING_MODEL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (object != null) {
            view.onGetDataSuccess(object);
        }
    }

    public void takePicture(FragmentManager fragmentManager, View _view) {
        if (!NetWorkInfo.isOnline(view.getActivity())) {
            view.onValidateError(_view, view.getActivity().getString(R.string.no_internet));
            return;
        }

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

    public void deletePicture(int id) {
        if (id != -1) {
            picture_id = id;
            cmd.execute();
        } else {
            view.onDeletePictureSuccess();
        }
    }

    public void deletePrice(final int position, final int id) {
        Log.e("price", "presenter " + position + "     " + id);
        view.showProgressBar(false, false, null, "Deleting price...");
        ParkingModel.getInstanse().deleteParkingPrice(id, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onDeletePriceSuccess(position);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionDeleteParkingPrices(position, id);
                else
                    view.onDeletePriceError(error);
            }
        });
    }

    private void getNewSessionDeleteParkingPrices(final int position, final int id) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                deletePrice(position, id);
            }

            @Override
            public void onError() {
                view.onDeletePriceError(new Error(2, "", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
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
            if (!NetWorkInfo.isOnline(view.getActivity())) {
                view.onValidateError(_view, view.getActivity().getString(R.string.no_internet));
                return;
            }

            if (object == null) {
                view.showProgressBar(false, false, null, view.getActivity().getString(R.string.adding));

                object = new ParkingInfo();
                object.setLat(placeModel.getLatitude());
                object.setLng(placeModel.getLongtitude());
                object.setType(transport_type);
                object.setAddress(placeModel.getAddress());
                object.setParking_name(parking_name);

                if (!begin_time.isEmpty())
                    object.setBegin_time(begin_time);
                if (!end_time.isEmpty())
                    object.setBegin_time(end_time);

                object.setTotal_place(total_place);
                object.setEmpty_number(total_place);
                object.setPrices(arrayList_price);
                object.setPictures(arrayList_picture);

                addParking(object);
            } else {
                view.showProgressBar(false, false, null, view.getActivity().getString(R.string.updating));

                object.setLat(placeModel.getLatitude());
                object.setLng(placeModel.getLongtitude());
                object.setType(transport_type);
                object.setAddress(placeModel.getAddress());
                object.setParking_name(parking_name);

                if (!begin_time.isEmpty())
                    object.setBegin_time(begin_time);
                if (!end_time.isEmpty())
                    object.setBegin_time(end_time);

                object.setTotal_place(total_place);
                object.setEmpty_number(total_place);

                object.setPrices(null);
                object.setPictures(null);

                Log.e("parking", "update");
                updateParking(object);
            }
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

    private void addParking(final ParkingInfo resp_parking_info) {
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
                    getNewSessionAddParking(resp_parking_info);
                else
                    view.onAddParkingError(error);
            }
        });
    }

    private void getNewSessionAddParking(final ParkingInfo resp_parking_info) {
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

    private void updateParking(final ParkingInfo resp_parking_info) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING;
        String jsonObject = JsonHelper.toJson(resp_parking_info);
        String session = LoginModel.getInstance().getSession();

        Log.e("ud_session", session);
        Log.e("ud_url", url);
        Log.e("ud_json", jsonObject);

        ParkingModel.getInstanse().updateParking(url, jsonObject, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onUpdateParkingSuccess(object);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionUpdateParking(resp_parking_info);
                else
                    view.onAddParkingError(error);
            }
        });
    }

    private void getNewSessionUpdateParking(final ParkingInfo resp_parking_info) {
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

    @SuppressWarnings("ConstantConditions")
    public void getTime(final boolean isBegin) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(view.getActivity(), R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                view.onGetTimeSuccess(isBegin, getHour(selectedHour), getMinute(selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time.
        mTimePicker.show();
    }

    private String getHour(int hour) {
        if (hour < 10)
            return "0" + hour;
        else
            return String.valueOf(hour);
    }

    private String getMinute(int minute) {
        if (minute < 10)
            return "0" + minute;
        else
            return String.valueOf(minute);
    }

    @Override
    public void getSessionError() {
        view.onDeletePictureError(new Error(2, "", view.getActivity().getString(R.string.error_session_invalid)));
    }
}