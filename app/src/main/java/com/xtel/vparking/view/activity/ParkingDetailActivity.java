package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xtel.vparking.R;
import com.xtel.vparking.view.activity.inf.IDetailView;

public class ParkingDetailActivity extends BasicActivity implements IDetailView {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        initToolbar(R.id.detail_toolbar, null);
        initViewpager();
    }

    private void initViewpager() {
//        TabLayout tabLayout = (TabLayout) TabLayout
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
