package com.xtel.vparking.view.fragment;

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
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.VerhiclePresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.AddVerhicleActivity;
import com.xtel.vparking.view.activity.inf.VerhicleView;
import com.xtel.vparking.view.adapter.VerhicleAdapter;
import com.xtel.vparking.view.fragment.BasicFragment;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/9/2016.
 */

public class VerhicleFragment extends BasicFragment implements VerhicleView {
    public static final int RESULT_ADD_VERHICLE = 66, REQUEST_ADD_VERHICLE = 99;
    public static final int RESULT_UPDATE_VERHICLE = 22;
    private VerhiclePresenter presenter;

    private RecyclerView recyclerView;
    private ArrayList<Verhicle> arrayList;
    private VerhicleAdapter adapter;
    private ProgressView progressView;

    private int position = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verhicle, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerview(view);
        initProgressView(view);

        presenter = new VerhiclePresenter(this);
        presenter.getAllVerhicle();
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.verhicle_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        adapter = new VerhicleAdapter(getActivity(), arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressView(View view) {
        progressView = new ProgressView(null, view);
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
    public void onGetVerhicleByIdSuccess(Verhicle verhicle) {
        Log.e(this.getClass().getSimpleName(), "insert position " + position);
        adapter.insertNewItem(position, verhicle);
        checkListData();
        position = -1;
    }

    @Override
    public void onItemClicked(int position, Verhicle verhicle) {
        this.position = position;
        startActivityForResult(AddVerhicleActivity.class, Constants.VERHICLE_MODEL, verhicle, REQUEST_ADD_VERHICLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(this.getClass().getSimpleName(), "request " + requestCode + " result " + resultCode);
        if (requestCode == REQUEST_ADD_VERHICLE) {
            if (resultCode == RESULT_ADD_VERHICLE || resultCode == RESULT_UPDATE_VERHICLE) {
                if (data != null) {
                    int id = data.getIntExtra(Constants.VERHICLE_ID, -1);
                    presenter.getVerhicleById(id);
                    Log.e(this.getClass().getSimpleName(), "result id: " + id);
                } else
                    Log.e(this.getClass().getSimpleName(), "result data null");
            } else
                position = -1;
        }
    }
}