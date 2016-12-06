package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.daimajia.swipe.util.Attributes;
import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Favotire;
import com.xtel.vparking.presenter.FavoritePresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.FavoriteView;
import com.xtel.vparking.view.adapter.FavoriteAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class FavoriteActivity extends BasicActivity implements FavoriteView {
    private ArrayList<Favotire> arrayList;
    private RecyclerView recyclerView;
    private ProgressView progressView;
    private FavoritePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initToolbar(R.id.favorite_toolbar, null);
        initRecyclerview();
        initProgressView();
        presenter = new FavoritePresenter(this);
        presenter.getParkingFavorite();
    }

    private void initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.favorite_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));

        arrayList = new ArrayList<>();
        RecyclerView.Adapter adapter = new FavoriteAdapter(FavoriteActivity.this, arrayList);
        ((FavoriteAdapter) adapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView() {
        progressView = new ProgressView(this, null);
        progressView.initData(R.mipmap.icon_parking, "Không có bãi đỗ yêu thích nào", "Kiểm tra lại", "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);
        progressView.showProgressbar();

        progressView.setProgressViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.showProgressbar();
                presenter.getParkingFavorite();
            }
        });
    }

    private void checkListData() {
        if (arrayList.size() == 0) {
            progressView.updateData(R.mipmap.icon_parking, "Không có bãi đỗ yêu thích nào", "Kiểm tra lại");
            progressView.showData();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            progressView.hide();
        }
    }

    @Override
    public void onGetParkingFavoriteSuccess(ArrayList<Favotire> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetParkingFavoriteError(Error error) {
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), "Thử lại");
        progressView.showData();
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
}
