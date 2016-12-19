package com.xtel.vparking.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.presenter.ManagementPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.ManagementView;
import com.xtel.vparking.view.adapter.ManagementAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class ManagementFragment extends BasicFragment implements ManagementView {
    private ArrayList<ParkingInfo> arrayList;
    private RecyclerView recyclerView;
    private ManagementAdapter managementAdapter;
    private ProgressView progressView;

    private ManagementPresenter presenter;
    public static final String PARKING_MODEL = "parking_model";
    public static final int REQUEST_UPDATE = 99, RESULT_UPDATE = 66;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_management, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new ManagementPresenter(this);
        initRecyclerview(view);
        initProgressView(view);
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_quan_ly_bai_do);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        managementAdapter = new ManagementAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(managementAdapter);
    }

    private void initProgressView(View view) {
        progressView = new ProgressView(null, view);
        progressView.initData(R.mipmap.icon_parking, "Bạn chưa có bãi đỗ nào nào", getString(R.string.touch_to_try_again), "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);

        progressView.onLayoutClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParkingByUser();
            }
        });

        progressView.onRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getParkingByUser();
            }
        });

        progressView.onSwipeLayoutPost(new Runnable() {
            @Override
            public void run() {
                getParkingByUser();
            }
        });
    }

    private void getParkingByUser() {
        progressView.hideData();
        progressView.setRefreshing(true);
        presenter.getParkingByUser();
    }

    private void checkListData() {
        progressView.setRefreshing(false);
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Bạn chưa có bãi đỗ nào", getString(R.string.touch_to_try_again));
            progressView.show();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    @Override
    public void onNetworkDisable() {
        progressView.setRefreshing(false);
        progressView.updateData(R.mipmap.ic_no_internet, getString(R.string.no_internet), getString(R.string.touch_to_try_again));
        progressView.showData();
    }

    @Override
    public void onGetParkingByUserSuccess(ArrayList<ParkingInfo> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetParkingByUserError(Error error) {
        progressView.setRefreshing(false);
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), getString(R.string.touch_to_try_again));
        progressView.showData();
    }

    @Override
    public void onGetParkingInfoSuccess(ParkingInfo parkingInfo) {
        managementAdapter.addNewItem(parkingInfo);
        checkListData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ADD_PARKING_REQUEST && resultCode == Constants.ADD_PARKING_RESULT) {
            int id = data.getIntExtra(Constants.INTENT_PARKING_ID, -1);

            if (id != -1) {
                presenter.getParkingInfo(id);
            }
        } else if (requestCode == REQUEST_UPDATE && resultCode == RESULT_UPDATE) {
            ParkingInfo parkingInfo = (ParkingInfo) data.getSerializableExtra(PARKING_MODEL);

            if (parkingInfo != null)
                managementAdapter.updateItem(parkingInfo);
        }
    }

    @Override
    public void onDestroy() {
        presenter.destroyView();
        super.onDestroy();
    }
}
