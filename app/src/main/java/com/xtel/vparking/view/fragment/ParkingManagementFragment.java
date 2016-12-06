package com.xtel.vparking.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.presenter.ManagementPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.ManagementView;
import com.xtel.vparking.view.adapter.ParkingManagementAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class ParkingManagementFragment extends BasicFragment implements ManagementView {
    private ArrayList<ParkingInfo> arrayList;
    private RecyclerView recyclerView;
    private ParkingManagementAdapter parkingManagementAdapter;
    private ProgressView progressView;

    private ManagementPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quan_ly_bai_do_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new ManagementPresenter(this);

        initRecyclerview(view);
        initProgressView(view);
        presenter.getParkingByUser();
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_quan_ly_bai_do);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        parkingManagementAdapter = new ParkingManagementAdapter(getContext(), arrayList);
        recyclerView.setAdapter(parkingManagementAdapter);
    }

    private void initProgressView(View view) {
        progressView = new ProgressView(null, view);
        progressView.initData(R.mipmap.icon_parking, "Bạn chưa có bãi đỗ nào nào", "Kiểm tra lại", "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);
        progressView.showProgressbar();

        progressView.setProgressViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.showProgressbar();
                presenter.getParkingByUser();
            }
        });
    }

    @Override
    public void onGetParkingByUserSuccess(ArrayList<ParkingInfo> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetParkingByUserError(Error error) {
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), "Thử lại");
        progressView.showData();
    }

    @Override
    public void onGetParkingInfoSuccess(ParkingInfo parkingInfo) {
        parkingManagementAdapter.addNewItem(parkingInfo);
        checkListData();
    }

    @Override
    public Activity getFragmentActivity() {
        return getActivity();
    }

    private void checkListData() {
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Bạn chưa có bãi đỗ nào", "Tải lại");
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
            int id = data.getIntExtra(Constants.INTENT_PARKING_ID, -1);

            Log.e("ql_result_id", "null k: " + id);
            if (id != -1) {
                presenter.getParkingInfo(id);
            }
        }
    }
}
