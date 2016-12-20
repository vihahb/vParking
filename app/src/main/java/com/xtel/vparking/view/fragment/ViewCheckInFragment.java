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
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.ParkingCheckIn;
import com.xtel.vparking.presenter.ViewCheckInPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.TichketActivity;
import com.xtel.vparking.view.activity.ViewParkingActivity;
import com.xtel.vparking.view.activity.inf.IViewCheckIn;
import com.xtel.vparking.view.adapter.ViewCheckInAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/19/2016.
 */

public class ViewCheckInFragment extends BasicFragment implements IViewCheckIn {
    public static final String CHECKED_OBJECT = "checked_object", CHECKED_ID = "checked_id";
    private ViewCheckInPresenter presenter;

    private RecyclerView recyclerView;
    private ViewCheckInAdapter adapter;
    private ArrayList<ParkingCheckIn> arrayList;
    private ProgressView progressView;
    private int parking_id = -1;

    public static ViewCheckInFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(Constants.ID_PARKING, id);
        ViewCheckInFragment fragment = new ViewCheckInFragment();
        fragment.setArguments(args);

        Log.e("vp", "id check in fragment " + id);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_check_in, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPresenter();
        initRecyclerview(view);
        initProgressView(view);
    }

    private void initPresenter() {
        try {
            parking_id = getArguments().getInt(Constants.ID_PARKING);
            Log.e("vp", "fragment get id " + parking_id);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("vp", "error get id " + parking_id);
        }

        presenter = new ViewCheckInPresenter(this, parking_id);
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.view_check_in_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        adapter = new ViewCheckInAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView(View view) {
        progressView = new ProgressView(null, view);
        progressView.initData(R.mipmap.icon_parking, "Không có phương tiện nào", getString(R.string.touch_to_try_again), "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);

        progressView.onLayoutClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllCheckIn();
            }
        });

        progressView.onRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllCheckIn();
            }
        });

        progressView.onSwipeLayoutPost(new Runnable() {
            @Override
            public void run() {
                getAllCheckIn();
            }
        });
    }

    private void getAllCheckIn() {
        progressView.hideData();
        progressView.setRefreshing(true);
        presenter.getCheckIn();
    }

    private void checkListData() {
        progressView.setRefreshing(false);
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Không có phương tiện nào", getString(R.string.touch_to_try_again));
            progressView.show();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    private void removeVerhicle(String transaction) {
        for (int i = (arrayList.size() - 1); i >= 0; i--) {
            if (arrayList.get(i).getTransaction().equals(transaction)) {
                adapter.removeItem(i);
                checkListData();
                return;
            }
        }
    }

    @Override
    public void showShortToast(String message) {
        super.showShortToast(message);
    }

    @Override
    public void onNetworkDisable() {
        progressView.setRefreshing(false);
        progressView.updateData(R.mipmap.ic_no_internet, getString(R.string.no_internet), getString(R.string.touch_to_try_again));
        progressView.showData();
    }

    @Override
    public void onGetVerhicleSuccess(ArrayList<ParkingCheckIn> arrayList) {
        this.arrayList.addAll(arrayList);
        if (arrayList.size() < 8)
            adapter.setLoadMore(false);
        checkListData();
    }

    @Override
    public void onGetVerhicleError(Error error) {
        progressView.setRefreshing(false);
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), getString(R.string.touch_to_try_again));
        progressView.showData();
    }

    @Override
    public void onEndlessScroll() {
        presenter.getCheckIn();
    }

    @Override
    public void onItemClicked(ParkingCheckIn checkIn) {
        Intent intent = new Intent(getActivity(), TichketActivity.class);
        intent.putExtra(Constants.ID_PARKING, parking_id);
        intent.putExtra(CHECKED_OBJECT, checkIn);
        getActivity().startActivityForResult(intent, ViewParkingActivity.REQUEST_VIEW);
    }

    @Override
    public void onDestroy() {
        presenter.destroyView();
        super.onDestroy();
    }
}