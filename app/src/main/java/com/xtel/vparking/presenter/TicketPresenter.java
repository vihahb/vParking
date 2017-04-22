package com.xtel.vparking.presenter;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.CheckInModel;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingCheckIn;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.view.activity.inf.ITichketView;
import com.xtel.vparking.view.fragment.CheckedFragment;

/**
 * Created by Lê Công Long Vũ on 12/14/2016
 */

public class TicketPresenter {
    private ITichketView view;
    private CheckIn checkIn;
    private ParkingCheckIn parkingCheckIn;
    private int parking_id = -1;

    public TicketPresenter(ITichketView view) {
        this.view = view;
    }

    public void getData() {
        try {
            checkIn = (CheckIn) view.getActivity().getIntent().getSerializableExtra(CheckedFragment.CHECKED_OBJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            parking_id = view.getActivity().getIntent().getIntExtra(Constants.ID_PARKING, -1);
            parkingCheckIn = (ParkingCheckIn) view.getActivity().getIntent().getSerializableExtra(CheckedFragment.CHECKED_OBJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkIn != null) {
            view.onGetDataSuccess(checkIn.getVehicle().getName(), checkIn.getCheckin_time(), checkIn.getVehicle().getPlate_number(), checkIn.getTicket_code());
            getParkingInfo(checkIn.getParking().getId());
        } else if (parkingCheckIn != null && parking_id != -1) {
            view.hideButton();
            view.onGetDataSuccess(parkingCheckIn.getVehicle().getName(), parkingCheckIn.getCheckin_time(), parkingCheckIn.getVehicle().getPlate_number(), parkingCheckIn.getTicket_code());
            getParkingInfo(parking_id);
        } else
            view.onGetDataError();
    }

    private void getParkingInfo(final int id) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + id;
        String session = LoginModel.getInstance().getSession();

        ParkingModel.getInstanse().getParkingInfo(url, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onGetParkingInfoSuccess(obj);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionParkingInfo(id);
                else
                    view.onGetParkingInfoError(error);
            }

            @Override
            public void onUpdate() {
                view.onUpdateVersion();
            }
        });
    }

    private void getNewSessionParkingInfo(final int id) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingInfo(id);
            }

            @Override
            public void onError() {
                view.onGetParkingInfoError(new Error(2, "ERROR", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }

    public void viewParking() {
        if (checkIn != null)
            view.onViewParking(checkIn.getParking().getId());
        else
            view.onViewParking(parking_id);
    }

    public void checkOut() {
        String url = Constants.SERVER_PARKING + Constants.PARKING_CHECK_OUT;
        String session = LoginModel.getInstance().getSession();

        CheckInModel.getInstance().checkOutVerhicle(url, JsonHelper.toJson(checkIn), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onCheckOutSuccess(checkIn);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionCheckOut();
                else
                    view.onCheckOutError(error);
            }

            @Override
            public void onUpdate() {
                view.onUpdateVersion();
            }
        });
    }

    private void getNewSessionCheckOut() {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                checkOut();
            }

            @Override
            public void onError() {
                view.onCheckOutError(new Error(2, "ERROR", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }
}