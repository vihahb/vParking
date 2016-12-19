package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.xtel.vparking.R;
import com.xtel.vparking.presenter.ViewParkingPresenter;
import com.xtel.vparking.view.activity.inf.IParkingView;
import com.xtel.vparking.view.adapter.ViewParkingAdapter;

public class ViewParkingActivity extends BasicActivity implements IParkingView {
    ViewParkingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parking);

        presenter = new ViewParkingPresenter(this);
        initToolbar(R.id.detail_toolbar, null);
        presenter.getData();
    }

    private void initViewpager(int id) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.detail_tablayout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        ViewParkingAdapter adapter = new ViewParkingAdapter(getSupportFragmentManager(), id);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onGetDataSuccess(int id) {
        initViewpager(id);
    }

    @Override
    public void onGetDataError() {
        showDialog(false, false, "Thông báo", "Có lỗi xảy ra, vui lòng thử lại", "OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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