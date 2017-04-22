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
import com.xtel.vparking.view.activity.inf.ScanQrView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Lê Công Long Vũ on 12/3/2016
 */

public class ScanQrPresenter extends BasicPresenter {
    private ScanQrView view;
    private CheckInVerhicle checkInVerhicle;
    private ArrayList<Verhicle> arrayList;

    private ICmd iCmd = new ICmd() {
        @Override
        public void execute(final Object... params) {
            VerhicleModel.getInstance().getAllVerhicle(new ResponseHandle<RESP_Verhicle_List>(RESP_Verhicle_List.class) {
                @Override
                public void onSuccess(RESP_Verhicle_List obj) {
                    arrayList = obj.getData();

                    if (arrayList.size() > 0) {
                        Collections.sort(arrayList, new Comparator<Verhicle>() {
                            @Override
                            public int compare(Verhicle lhs, Verhicle rhs) {
                                try {
                                    return String.valueOf(lhs.getFlag_default()).compareTo(String.valueOf(rhs.getFlag_default()));
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });
                        Collections.reverse(arrayList);
                    } else {
                        arrayList.add(new Verhicle(-1, "", 4, "Bạn không có xe nào", "", 2, null));
                    }

                    view.onGetVerhicleSuccess(arrayList);
                }

                @Override
                public void onError(Error error) {
                    if (error.getCode() == 2)
                        getNewSession(view.getActivity(), iCmd, params);
                    else
                        view.onGetVerhicleError(error);
                }

                @Override
                public void onUpdate() {
                    view.onUpdateVersion();
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
        if (arrayList.get(position).getId() == -1) {
            view.onCheckingError(new Error(0, "ERROR", view.getActivity().getString(R.string.error)));
            return;
        }

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

            @Override
            public void onUpdate() {
                view.onUpdateVersion();
            }
        });
    }

    @Override
    public void getSessionError() {

    }
}