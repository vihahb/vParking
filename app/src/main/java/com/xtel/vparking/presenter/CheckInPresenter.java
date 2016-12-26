package com.xtel.vparking.presenter;

import android.os.AsyncTask;
import android.os.Handler;

import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.VerhicleModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Verhicle_List;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.activity.inf.CheckInView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class CheckInPresenter {
    private CheckInView view;

    public CheckInPresenter(CheckInView view) {
        this.view = view;
    }

    public void getAllVerhicle() {
        if (!NetWorkInfo.isOnline(view.getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.onNetworkDisable();
                }
            }, 500);

            return;
        }

        VerhicleModel.getInstance().getAllVerhicle(new ResponseHandle<RESP_Verhicle_List>(RESP_Verhicle_List.class) {
            @Override
            public void onSuccess(RESP_Verhicle_List obj) {
                sortVerhicle(obj.getData());
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
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getAllVerhicle();
            }

            @Override
            public void onError() {
                view.onGetVerhicleError(new Error(2, "ERROR", "Bạn đã hết phiên làm việc"));
            }
        });
    }

    private void sortVerhicle(final ArrayList<Verhicle> arrayList) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (arrayList.size() > 0) {
                    ArrayList<Verhicle> arr_car = new ArrayList<>();
                    ArrayList<Verhicle> arr_moto = new ArrayList<>();

                    for (int i = (arrayList.size() - 1); i >= 0; i--) {
                        if (arrayList.get(i).getType() == 1) {
                            arr_car.add(arrayList.get(i));
                        } else {
                            arr_moto.add(arrayList.get(i));
                        }
                    }

                    arrayList.clear();

                    if (arr_car.size() > 0) {
                        Collections.sort(arr_car, new Comparator<Verhicle>() {
                            @Override
                            public int compare(Verhicle lhs, Verhicle rhs) {
                                try {
                                    return String.valueOf(lhs.getFlag_default()).compareTo(String.valueOf(rhs.getFlag_default()));
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                        Collections.reverse(arr_car);
                        arr_car.add(0, new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
                        arrayList.addAll(arr_car);
                    }

                    if (arr_moto.size() > 0) {
                        Collections.sort(arr_moto, new Comparator<Verhicle>() {
                            @Override
                            public int compare(Verhicle lhs, Verhicle rhs) {
                                try {
                                    return String.valueOf(lhs.getFlag_default()).compareTo(String.valueOf(rhs.getFlag_default()));
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                        Collections.reverse(arr_moto);
                        arr_moto.add(0, new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
                        arrayList.addAll(arr_moto);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                view.onGetVerhicleSuccess(arrayList);
            }
        }.execute();
    }
}