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
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.VerhicleCheckedPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.VerhicleCheckedView;
import com.xtel.vparking.view.adapter.CheckedAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;

public class VerhicleCheckedFragment extends BasicFragment implements VerhicleCheckedView {
    public static final int RESULT_CHECK_IN = 66, REQUEST_CHECK_IN = 99;
    private VerhicleCheckedPresenter presenter;

    private RecyclerView recyclerView;
    private ArrayList<CheckIn> arrayList;
    private ProgressView progressView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verhicle_checked, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerview(view);
        initProgressView(view);

        presenter = new VerhicleCheckedPresenter(this);
        presenter.getAllVerhicle();
    }

    private void initRecyclerview(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.checked_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        CheckedAdapter adapter = new CheckedAdapter(getActivity(), arrayList, this);
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
    public void onGetVerhicleSuccess(ArrayList<CheckIn> arrayList) {
        this.arrayList.addAll(arrayList);
        checkListData();
    }

    @Override
    public void onGetVerhicleError(Error error) {
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), "Kiểm tra lại");
        progressView.showData();
    }

    @Override
    public void onItemClicked(CheckIn checkIn) {
//        startActivityForResult(ScanQrActivity.class, CHECK_IN_OBJECT, verhicle, REQUEST_CHECK_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_IN && resultCode == RESULT_CHECK_IN) {
            if (data != null) {
                int id = data.getIntExtra(Constants.VERHICLE_ID, -1);
                Log.e(this.getClass().getSimpleName(), "result id: " + id);
            } else
                Log.e(this.getClass().getSimpleName(), "result data null");
        }
    }
}
