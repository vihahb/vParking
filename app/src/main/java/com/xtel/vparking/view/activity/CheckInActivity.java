package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.CheckInVerhicle;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.CheckInPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.CheckInView;
import com.xtel.vparking.view.adapter.CheckInAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class CheckInActivity extends BasicActivity implements CheckInView {
    private CheckInPresenter presenter;
    public static final int RESULT_CHECK_IN = 66, REQUEST_CHECK_IN = 99;
    public static final String CHECK_IN_OBJECT = "check_in_object";

    private RecyclerView recyclerView;
    private ArrayList<Verhicle> arrayList;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        presenter = new CheckInPresenter(this);
        initToolbar(R.id.checkin_toolbar, null);
        initRecyclerview();
        initProgressView();
    }

    private void initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.checkin_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        CheckInAdapter adapter = new CheckInAdapter(this, arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView() {
        progressView = new ProgressView(this, null);
        progressView.initData(R.mipmap.icon_parking, "Không có phương tiện nào", getString(R.string.touch_to_try_again), "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);

        progressView.onLayoutClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.hideData();
                progressView.setRefreshing(true);
                presenter.getAllVerhicle();
            }
        });

        progressView.onRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressView.hideData();
                progressView.setRefreshing(true);
                presenter.getAllVerhicle();
            }
        });

        progressView.onSwipeLayoutPost(new Runnable() {
            @Override
            public void run() {
                progressView.hideData();
                progressView.setRefreshing(true);
                presenter.getAllVerhicle();
            }
        });
    }

    private void checkListData() {
        progressView.setRefreshing(false);
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Không có phương tiện nào", getString(R.string.touch_to_try_again));
            progressView.showData();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    @Override
    public void onGetVerhicleSuccess(ArrayList<Verhicle> arrayList) {
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
    public void onItemClicked(CheckInVerhicle checkInVerhicle) {
        startActivityForResult(CheckOutActivity.class, CHECK_IN_OBJECT, checkInVerhicle, REQUEST_CHECK_IN);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_IN && resultCode == RESULT_CHECK_IN) {
            finish();
        }
    }
}
