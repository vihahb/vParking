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
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.presenter.ViewCheckInPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.CheckOutActivity;
import com.xtel.vparking.view.activity.inf.IViewCheckIn;
import com.xtel.vparking.view.adapter.ViewCheckInAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/19/2016.
 */

public class ViewCheckInFragment extends BasicFragment implements IViewCheckIn {
    public static final int RESULT_CHECK_OUT = 66, REQUEST_CHECKED = 99;
    public static final String CHECKED_OBJECT = "checked_object", CHECKED_ID = "checked_id";
    private ViewCheckInPresenter presenter;

    private RecyclerView recyclerView;
    private ViewCheckInAdapter adapter;
    private ArrayList<CheckIn> arrayList;
    private ProgressView progressView;

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
        int id = -1;

        try {
            id = getArguments().getInt(Constants.ID_PARKING);
            Log.e("vp", "fragment get id " + id);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("vp", "error get id " + id);
        }

        presenter = new ViewCheckInPresenter(this, id);
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
    public void onGetVerhicleSuccess(ArrayList<CheckIn> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetVerhicleError(Error error) {
        progressView.setRefreshing(false);
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), getString(R.string.touch_to_try_again));
        progressView.showData();
    }

    @Override
    public void onItemClicked(CheckIn checkIn) {
        startActivityForResult(CheckOutActivity.class, CHECKED_OBJECT, checkIn, REQUEST_CHECKED);
    }

    @Override
    public void onDestroy() {
        presenter.destroyView();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECKED) {
            if (resultCode == RESULT_CHECK_OUT) {
                if (data != null) {
                    String transaction = data.getStringExtra(CHECKED_ID);
                    removeVerhicle(transaction);
                }
            }
        }
    }
}