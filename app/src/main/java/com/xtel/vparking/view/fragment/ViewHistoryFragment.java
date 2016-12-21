package com.xtel.vparking.view.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.CheckInHisObj;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.HistoryModel;
import com.xtel.vparking.presenter.HistoryPresenter;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.RecyclerOnScrollListener;
import com.xtel.vparking.view.activity.inf.IViewHistory;
import com.xtel.vparking.view.adapter.ViewHistoryAdapter;
import com.xtel.vparking.view.widget.ProgressView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mr. M.2 on 12/19/2016.
 */

public class ViewHistoryFragment extends BasicFragment implements IViewHistory {

    private RecyclerView recyclerViewHistory;
    private ViewHistoryAdapter viewHistoryAdapter;
    private HistoryPresenter presenter;
    private ArrayList<CheckInHisObj> checkInHisArr;
    private BottomNavigationView bottomNavigationView;
    private ProgressView progressView;

    private TextView txt_time;
    private ImageView img_cal;

    //Varialbe for Fragment
    int year, month, day;

    //Variable for Request
    String time_now;


    public static ViewHistoryFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(Constants.ID_PARKING, id);
        ViewHistoryFragment fragment = new ViewHistoryFragment();
        fragment.setArguments(args);

        Log.e("vp", "id history fragment " + id);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPresenter();
        initRecycleView(view);
        initViewProgress(view);
        initView(view);
//        getData();

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
        presenter = new HistoryPresenter(this, id);
    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        time_now = year + "/" + (month + 1) + "/" + day;
        return time_now;
    }

    private void getData() {
        progressView.hideData();
        progressView.setRefreshing(true);
        initData(getTime());
    }

    private void initData(String time) {
        presenter.getAllData(time);
    }

    private void initRecycleView(View view) {
        recyclerViewHistory = (RecyclerView) view.findViewById(R.id.view_history_recyclerview);
        recyclerViewHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewHistory.setLayoutManager(layoutManager);
        checkInHisArr = new ArrayList<>();
        viewHistoryAdapter = new ViewHistoryAdapter(checkInHisArr, this);
        recyclerViewHistory.setAdapter(viewHistoryAdapter);
        recyclerViewHistory.getAdapter().notifyDataSetChanged();

        recyclerViewHistory.addOnScrollListener(new RecyclerOnScrollListener(layoutManager) {
            @Override
            public void onHide() {
                hideBottomView();
            }

            @Override
            public void onShow() {
                showBottomView();
            }

            @Override
            public void onLoadMore() {
                getData();
            }
        });
    }

    private void hideBottomView() {
        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showBottomView() {
        bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    private void initView(View view) {
        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.detail_bottom_navigation_view);
        txt_time = (TextView) view.findViewById(R.id.txt_time);
        img_cal = (ImageView) view.findViewById(R.id.img_cal);
        txt_time.setText(getTime());
        img_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnTime();
            }
        });
    }

    private void setOnTime() {
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), R.style.TimePicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String time_set = year + "/" + (month + 1) + "/" + dayOfMonth;
                txt_time.setText(time_set);
                checkInHisArr.clear();
                presenter.setPageDefault();
                viewHistoryAdapter.notifyDataSetChanged();
                initData(time_set);
            }
        }, year, month, day);
        pickerDialog.show();
    }

    private void initViewProgress(final View view) {
        progressView = new ProgressView(null, view);
        progressView.initData(R.mipmap.icon_parking, "Không có phương tiện nào", getString(R.string.touch_to_try_again), "Đang tải dữ liệu", Color.parseColor("#5c5ca7"));
        progressView.setUpWithView(recyclerViewHistory);

        progressView.onLayoutClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        progressView.onRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        progressView.onSwipeLayoutPost(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
    }

    private void checkListData() {
        progressView.disableSwipe();
        progressView.setRefreshing(false);
        showShortToast("3");
        if (checkInHisArr.size() == 0) {
            showShortToast("4");
            progressView.updateData(R.mipmap.icon_parking, "Không có phương tiện nào", getString(R.string.touch_to_try_again));
            progressView.show();
        } else {
            showShortToast("5");
            recyclerViewHistory.getAdapter().notifyDataSetChanged();
            progressView.hide();
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
    public void onGetHistorySuccess(ArrayList<CheckInHisObj> arrayList) {
        showShortToast("1");
        checkInHisArr.addAll(arrayList);
        if (arrayList.size() < 8)
            viewHistoryAdapter.setLoadMore(false);
        checkListData();
        showShortToast("2");
    }

    @Override
    public void onGetHistoryError(Error error) {
        progressView.setRefreshing(false);
        progressView.updateData(R.mipmap.icon_parking, JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_coloi)), getString(R.string.touch_to_try_again));
        progressView.showData();
    }

    @Override
    public void onItemClicked(CheckInHisObj checkInHisObj) {

    }

    @Override
    public void onEndlessScroll() {
        initData(getTime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroyViewing();
    }
}
