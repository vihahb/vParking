package com.xtel.vparking.presenter;

import android.util.Log;
import android.widget.Toast;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.Brandname;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Verhicle;
import com.xtel.vparking.model.entity.RESP_Verhicle_List;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.MyApplication;
import com.xtel.vparking.view.activity.inf.VerhicleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class VerhiclePresenter {
    private VerhicleView view;

    public VerhiclePresenter(VerhicleView view) {
        this.view = view;
    }

    private ArrayList<Verhicle> demoData() {
        ArrayList<Verhicle> arrayList = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Verhicle verhicle;
            Brandname brandname = new Brandname("code", "name", "made by");

            if (i % 2 == 0) {
                verhicle = new Verhicle(i, i+i+i+i+"-"+i+i+i+i, 2, "Xe máy " + i, "abc", 1, brandname);
            } else
                verhicle = new Verhicle(i, i+i+i+i+"-"+i+i+i+i, 1, "Xe Ô tô " + i, "abc", 0, brandname);

            arrayList.add(verhicle);
        }
        return arrayList;
    }

    public void getAllVerhicle() {
        String url = Constants.SERVER_PARKING + Constants.PARKING_VERHICLE;
        String session = LoginModel.getInstance().getSession();
        VerhicleModel.getInstance().getAllVerhicle(url, session, new ResponseHandle<RESP_Verhicle_List>(RESP_Verhicle_List.class) {
            @Override
            public void onSuccess(RESP_Verhicle_List obj) {
//                sortVerhicle(obj.getData());
                sortVerhicle(demoData());
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionVerhicle();
                else
                    view.onGetVerhicleError(error);
            }
        });
    }

    private void getNewSessionVerhicle() {
        Log.e("verhicle", "get new session");
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                Log.e("verhicle", "get new session success");
                getAllVerhicle();
            }

            @Override
            public void onError() {
                Log.e("verhicle", "get new session error");
                view.onGetVerhicleError(new Error(2, "ERROR", "Bạn đã hết phiên làm việc"));
            }
        });
    }

    private void sortVerhicle(ArrayList<Verhicle> arrayList) {
        Collections.sort(arrayList, new Comparator<Verhicle>() {
            @Override
            public int compare(Verhicle lhs, Verhicle rhs) {
                try {
                    return String.valueOf(lhs.getType()).compareTo(String.valueOf(rhs.getType()));
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        if (arrayList.size() > 0) {
            if (arrayList.get(0).getType() == 1) {
                arrayList.add(0, new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
            } else {
                arrayList.add(0, new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
            }

            for (int i = (arrayList.size() - 1); i > 0; i--) {
                if (arrayList.get(i).getType() != arrayList.get((i - 1)).getType()) {
                    if (arrayList.get(0).getType() == 1) {
                        arrayList.add(i, new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
                    } else {
                        arrayList.add(i, new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
                    }
                    break;
                }
            }
        }

        Log.e("verhicle", "total array " + arrayList.size());

        view.onGetVerhicleSuccess(arrayList);
    }

    public void getVerhicleById(final int id) {
        if (id == -1)
            return;

        String url = Constants.SERVER_PARKING + Constants.PARKING_VERHICLE_BY_ID + id;
        String session = LoginModel.getInstance().getSession();

        VerhicleModel.getInstance().getVerhicleById(url, session, new ResponseHandle<RESP_Verhicle>(RESP_Verhicle.class) {
            @Override
            public void onSuccess(RESP_Verhicle obj) {
                Verhicle verhicle = new Verhicle();
                verhicle.setId(obj.getId());
                verhicle.setPlate_number(obj.getPlate_number());
                verhicle.setType(obj.getType());
                verhicle.setName(obj.getName());
                verhicle.setDesc(obj.getDesc());
                verhicle.setFlag_default(obj.getFlag_default());
                verhicle.setBrandname(obj.getBrandname());
                view.onGetVerhicleByIdSuccess(verhicle);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionVerhicleById(id);
            }
        });
    }

    private void getNewSessionVerhicleById(final int id) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getVerhicleById(id);
            }

            @Override
            public void onError() {

            }
        });
    }
}
