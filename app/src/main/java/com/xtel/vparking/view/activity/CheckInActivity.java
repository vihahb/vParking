package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.CheckInPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.CheckInView;
import com.xtel.vparking.view.adapter.CheckInAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class CheckInActivity extends BasicActivity implements CheckInView {
    public static final int RESULT_CHECK_IN = 66, REQUEST_CHECK_IN = 99;
    private CheckInPresenter presenter;

    private RecyclerView recyclerView;
    private ArrayList<Verhicle> arrayList;
    private CheckInAdapter adapter;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        initToolbar(R.id.checkin_toolbar, null);
        initRecyclerview();
        initProgressView();

        presenter = new CheckInPresenter(this);
        presenter.getAllVerhicle();
    }

    private void initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.checkin_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        adapter = new CheckInAdapter(this, arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView() {
        progressView = new ProgressView(this, null);
        progressView.initData(R.mipmap.icon_parking, "Không có phương tiện nào", "Kiểm tra lại", "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);
        progressView.showProgressbar();

        progressView.setButtonwClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.showProgressbar();
                presenter.getAllVerhicle();
            }
        });
    }

    private void checkListData() {
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Không có phương tiện nào", "Kiểm tra lại");
            progressView.showData();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetVerhicleSuccess(ArrayList<Verhicle> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetVerhicleError(Error error) {
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), "Kiểm tra lại");
        progressView.showData();
    }

    @Override
    public void onItemClicked(Verhicle verhicle) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_IN && resultCode == RESULT_CHECK_IN) {
            if (data != null) {
                int id = data.getIntExtra(Constants.VERHICLE_ID, -1);
                Log.e(this.getClass().getSimpleName(), "result id: " + id);
            } else
                Log.e(this.getClass().getSimpleName(), "result data null");
        }
    }
}
