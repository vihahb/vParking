package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Verhicle;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.VerhiclePresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.VerhicleView;
import com.xtel.vparking.view.adapter.VerhicleAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class VerhicleActivity extends BasicActivity implements VerhicleView {
    public static final int RESULT_ADD_VERHICLE = 66, REQUEST_ADD_VERHICLE = 99;
    private VerhiclePresenter presenter;

    private RecyclerView recyclerView;
    private ArrayList<Verhicle> arrayList;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verhicle);

        initToolbar(R.id.verhicle_toolbar, null);
        initRecyclerview();
        initProgressView();

        presenter = new VerhiclePresenter(this);
        presenter.getAllVerhicle();
    }

    private void initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.verhicle_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        VerhicleAdapter adapter = new VerhicleAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView() {
        progressView = new ProgressView(this, null);
        progressView.initData(R.mipmap.icon_parking, "Bạn chưa có bãi đỗ nào nào", "Kiểm tra lại", "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
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
            progressView.updateData(R.mipmap.icon_parking, "Bạn chưa có bãi đỗ nào", "Kiểm tra lại");
            progressView.showData();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verhicle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_verhicle) {
            startActivityForResult(AddVerhicleActivity.class, REQUEST_ADD_VERHICLE);
        } else if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetVerhicleSuccess(RESP_Verhicle obj) {
        arrayList.addAll(obj.getData());
        checkListData();
    }

    @Override
    public void onGetVerhicleError(Error error) {
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), "Kiểm tra lại");
        progressView.showData();
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
