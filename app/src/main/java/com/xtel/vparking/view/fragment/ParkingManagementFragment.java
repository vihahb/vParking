package com.xtel.vparking.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.ParkingInfoModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.adapter.QuanLyBaiDoAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParkingManagementFragment extends Fragment {
    private ArrayList<ParkingInfoModel> arrayList;
    private RecyclerView recyclerView;
    private QuanLyBaiDoAdapter quanLyBaiDoAdapter;
    private ProgressView progressView;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quan_ly_bai_do_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gson = new Gson();

        initRecyclerview(view);
        initWidget(view);
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_quan_ly_bai_do);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        quanLyBaiDoAdapter = new QuanLyBaiDoAdapter(getContext(), arrayList);
        recyclerView.setAdapter(quanLyBaiDoAdapter);
    }

    private void initWidget(View view) {
        progressView = new ProgressView(null, view);
        progressView.initData(R.mipmap.icon_parking, "Bạn chưa có RESP_Parking nào", "Kiểm tra lại", "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);
        progressView.showProgressbar();

        progressView.setProgressViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.showProgressbar();
                new InitData().execute();
            }
        });

        new InitData().execute();
    }

    private class InitData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
                String url = Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING;

                Log.e(this.getClass().getSimpleName(), url + "       " + session);

                Request request = new Request.Builder()
                        .url(url)
                        .header(Constants.JSON_SESSION, session)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null) {
                progressView.updateData(R.mipmap.icon_parking, getString(R.string.error_server_request), "Thử lại");
                progressView.showData();
            } else {
                Log.e("ql_result_outside", s);
                Error errorModel = JsonParse.checkError(s);
                if (errorModel != null) {
                    if (errorModel.getCode() == 2)
                        getNewSessionAddParking();
                    else {
                        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(errorModel.getCode(), getString(R.string.loi_coloi)), "Thử lại");
                        progressView.showData();
                    }
                } else {
                    ArrayList<ParkingInfoModel> arr = gson.fromJson(s, new TypeToken<ArrayList<ParkingInfoModel>>() {
                    }.getType());
                    arrayList.addAll(arr);
                    arr.clear();
                    checkListData();
                }
            }
        }
    }

    private void getNewSessionAddParking() {
        GetNewSession.getNewSession(getContext(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                new InitData().execute();
            }

            @Override
            public void onError() {
                progressView.updateData(R.mipmap.icon_parking, "Đã xảy ra lỗi", "Thử lại");
                progressView.showData();
            }
        });
    }

    private void checkListData() {
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Bạn chưa có RESP_Parking nào", "Tải lại");
            progressView.showData();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("ql_result", "null k: " + requestCode + "        " + resultCode);

        if (requestCode == Constants.ADD_PARKING_REQUEST && resultCode == Constants.ADD_PARKING_RESULT) {
            String id = data.getStringExtra(Constants.INTENT_PARKING_ID);

            Log.e("ql_result_id", "null k: " + id);
            if (id != null) {
                new GetParkingInfo().execute(id);
            }
        }

//        super.onActivityResult(requestCode, resultCode, data);
    }

    private class GetParkingInfo extends AsyncTask<String, Void, String> {
        private String id;

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            this.id = params[0];

            try {
                String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + params[0];
                String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);

                Request request = new Request.Builder()
                        .url(url)
                        .header(Constants.JSON_SESSION, session)
                        .build();
                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (Exception e) {
                Log.e("pk_loi", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                Log.e("ql_pk_info", "null k: " + s);
                Error errorModel = JsonParse.checkError(s);
                if (errorModel != null) {
                    if (errorModel.getCode() == 2 && id != null) {
                        getNewSessiongGetParking(id);
                    }
                } else {
                    final ParkingInfoModel parkingInfoModel = gson.fromJson(s, new TypeToken<ParkingInfoModel>() {
                    }.getType());

                    if (parkingInfoModel != null) {
                        arrayList.add(parkingInfoModel);
                        quanLyBaiDoAdapter.addNewItem(parkingInfoModel);
                        checkListData();
                    }
                }
            }
        }
    }

    private void getNewSessiongGetParking(final String id) {
        GetNewSession.getNewSession(getContext(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                new GetParkingInfo().execute(id);
            }

            @Override
            public void onError() {

            }
        });
    }
}
