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
import com.xtel.vparking.model.entity.RESP_Verhicle;
import com.xtel.vparking.model.entity.RESP_Verhicle_List;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.activity.inf.VerhicleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class VerhiclePresenter {
    private VerhicleView view;
    private boolean isViewing = true;

    public VerhiclePresenter(VerhicleView view) {
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

        String url = Constants.SERVER_PARKING + Constants.PARKING_VERHICLE;
        String session = LoginModel.getInstance().getSession();
        VerhicleModel.getInstance().getAllVerhicle(new ResponseHandle<RESP_Verhicle_List>(RESP_Verhicle_List.class) {
            @Override
            public void onSuccess(RESP_Verhicle_List obj) {
                if (isViewing)
                    sortVerhicle(obj.getData());
            }

            @Override
            public void onError(Error error) {
                if (isViewing)
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
                if (isViewing)
                    getAllVerhicle();
            }

            @Override
            public void onError() {
                if (isViewing)
                    view.onGetVerhicleError(new Error(2, "ERROR", "Bạn đã hết phiên làm việc"));
            }
        });
    }

    private void sortVerhicle(final ArrayList<Verhicle> arrayList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
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
                        if (arrayList.get((i - 1)).getType() < 10)
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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (isViewing)
                    view.onGetVerhicleSuccess(arrayList);
            }
        }.execute();
    }

    public void getVerhicleById(final int id) {
        if (id == -1)
            return;

        String url = Constants.SERVER_PARKING + Constants.PARKING_VERHICLE_BY_ID + id;
        String session = LoginModel.getInstance().getSession();

        VerhicleModel.getInstance().getVerhicleById(url, session, new ResponseHandle<RESP_Verhicle>(RESP_Verhicle.class) {
            @Override
            public void onSuccess(RESP_Verhicle obj) {
                if (isViewing) {
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
                if (isViewing)
                    getVerhicleById(id);
            }

            @Override
            public void onError() {
            }
        });
    }

    public void destroyView() {
        isViewing = false;
    }
}
