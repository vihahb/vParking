package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.xtel.vparking.R;
import com.xtel.vparking.presenter.ViewParkingPresenter;
import com.xtel.vparking.view.activity.inf.IParkingView;
import com.xtel.vparking.view.adapter.ViewParkingAdapter;
import com.xtel.vparking.utils.PageTransformer;
import com.xtel.vparking.view.widget.ViewPagerNoScroll;

public class ViewParkingActivity extends BasicActivity implements BottomNavigationView.OnNavigationItemSelectedListener, IParkingView {
    private ViewParkingPresenter presenter;
    private ActionBar actionBar;
    public static BottomNavigationView bottomNavigationView;
    private ViewPagerNoScroll viewPager;

    public static final int REQUEST_VIEW = 99, RESULT_VIEW = 88;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parking);

        presenter = new ViewParkingPresenter(this);
        initToolbar(R.id.detail_toolbar, null);
        actionBar = getSupportActionBar();
        presenter.getData();
    }

    private void initViewpager(int id) {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.detail_bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        viewPager = (ViewPagerNoScroll) findViewById(R.id.detail_viewpager);
        viewPager.setPagingEnabled(false);
        ViewParkingAdapter adapter = new ViewParkingAdapter(getSupportFragmentManager(), id);
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer(false, new PageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        actionBar.setTitle(getString(R.string.title_activity_view_check_in));
                        break;
                    case 1:
                        actionBar.setTitle(getString(R.string.title_activity_view_history));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIEW && resultCode == RESULT_VIEW) {
            setResult(HomeActivity.RESULT_FIND, data);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_detail_check_in:
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_detail_history:
                viewPager.setCurrentItem(1);
                break;
            default:
                return false;
        }
        return true;
    }
}