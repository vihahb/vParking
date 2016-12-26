package com.xtel.vparking.presenter;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.ICmd;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.CheckInModel;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.CheckInVerhicle;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.model.entity.RESP_Verhicle_List;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.view.activity.CheckInActivity;
import com.xtel.vparking.view.activity.inf.ScanQrView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/3/2016.
 */

public class ScanQrPresenter extends BasicPresenter {
    private ScanQrView view;
    private CheckInVerhicle checkInVerhicle;
    private ArrayList<Verhicle> arrayList;

    private ICmd iCmd = new ICmd() {
        @Override
        public void execute(Object... params) {
            VerhicleModel.getInstance().getAllVerhicle(new ResponseHandle<RESP_Verhicle_List>(RESP_Verhicle_List.class) {
                @Override
                public void onSuccess(RESP_Verhicle_List obj) {
                    arrayList = obj.getData();
                    if (arrayList.size() > 0)
                        view.onGetVerhicleSuccess(obj.getData());
                    else
                        view.onGetVerhicleError(new Error(-1, "error", view.getActivity().getString(R.string.error)));
                }

                @Override
                public void onError(Error error) {
                    if (error.getCode() == 2)
                        getNewSession(view.getActivity(), iCmd);
                    else
                        view.onGetVerhicleError(error);
                }
            });
        }
    };

    public ScanQrPresenter(ScanQrView scanQrView) {
        this.view = scanQrView;
        checkInVerhicle = new CheckInVerhicle();
    }

    public void getVerhicle() {
        iCmd.execute();
    }

    public void startCheckIn(int position, String gift_code, String content) {
        view.onStartChecking();
        String url = Constants.SERVER_PARKING + Constants.PARKING_CHECK_IN;
        String session = LoginModel.getInstance().getSession();

        checkInVerhicle.setVerhicle_id(arrayList.get(position).getId());
        checkInVerhicle.setCheckin_type(arrayList.get(position).getType());
        checkInVerhicle.setParking_code(content);

        CheckInModel.getInstance().checkInVerhicle(url, JsonHelper.toJson(checkInVerhicle), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onCheckingSuccess();
            }

            @Override
            public void onError(Error error) {
                view.onCheckingError(error);
            }
        });
    }

    @Override
    public void getSessionError() {

    }
}