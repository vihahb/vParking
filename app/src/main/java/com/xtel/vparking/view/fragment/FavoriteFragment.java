package com.xtel.vparking.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;
import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Favotire;
import com.xtel.vparking.presenter.FavoritePresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.FavoriteView;
import com.xtel.vparking.view.adapter.FavoriteAdapter;
import com.xtel.vparking.view.fragment.BasicFragment;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class FavoriteFragment extends BasicFragment implements FavoriteView {
    private ArrayList<Favotire> arrayList;
    private RecyclerView recyclerView;
    private ProgressView progressView;
    private FavoritePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerview(view);
        initProgressView(view);
        presenter = new FavoritePresenter(this);
        presenter.getParkingFavorite();
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.favorite_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        RecyclerView.Adapter adapter = new FavoriteAdapter(getActivity(), arrayList, this);
        ((FavoriteAdapter) adapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView(View view) {
        progressView = new ProgressView(null, view);
        progressView.initData(R.mipmap.icon_parking, "Không có bãi đỗ yêu thích nào", "Kiểm tra lại", "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerView);
        progressView.showProgressbar();

        progressView.setButtonwClicked(new View.OnClickListener() {
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
    public void showShortToast(String message) {
        super.showShortToast(message);
    }

    @Override
    public void showLongToast(String message) {
        super.showLongToast(message);
    }

    @Override
    public void onGetParkingFavoriteSuccess(ArrayList<Favotire> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetParkingFavoriteError(Error error) {
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), "Kiểm tra lại");
        progressView.showData();
    }
}