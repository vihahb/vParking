package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.xtel.vparking.R;
import com.xtel.vparking.view.activity.inf.IViewParking;
import com.xtel.vparking.view.adapter.ViewParkingAdapter;

public class ViewParkingActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        initToolbar(R.id.detail_toolbar, null);
        initViewpager();
    }

    private void initViewpager() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.detail_tablayout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        ViewParkingAdapter adapter = new ViewParkingAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}