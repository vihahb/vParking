package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.presenter.HomePresenter;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.inf.HomeView;
import com.xtel.vparking.view.fragment.HomeFragment;
import com.xtel.vparking.view.fragment.ParkingManagementFragment;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class HomeActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        HomeView {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView img_avatar;
    private TextView txt_name;
    private Button btn_active_master;
    private Menu menu;
    private HomePresenter homePresenter;

    private final String HOME_FRAGMENT = "home_fragment", MANAGER_FRAGMENT = "manager_fragment";
    public static final int REQUEST_CODE = 99;
    public static final int RESULT_GUID = 88;
    public static int PARKING_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        replaceFragment(R.id.home_layout_content, new HomeFragment(), HOME_FRAGMENT);

        initView();
        initNavigation();
        initListener();
        homePresenter = new HomePresenter(this);
    }

    private void initView() {
        drawer = (DrawerLayout) findViewById(R.id.home_drawer);
        navigationView = (NavigationView) findViewById(R.id.home_navigationview);
        btn_active_master = (Button) findViewById(R.id.home_btn_active);

        View view = navigationView.getHeaderView(0);
        img_avatar = (ImageView) view.findViewById(R.id.header_img_avatar);
        txt_name = (TextView) view.findViewById(R.id.header_txt_name);
    }

    private void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initListener() {
        navigationView.setNavigationItemSelectedListener(this);
        img_avatar.setOnClickListener(this);
        btn_active_master.setOnClickListener(this);
    }

    private void setParkingMaster() {
        navigationView.getMenu().findItem(R.id.nav_parking_quanlytaikhoan).setVisible(true);
        btn_active_master.setEnabled(false);
        btn_active_master.setAlpha(0.6f);
    }

    @Override
    public void showLongToast(String message) {
        super.showLongToast(message);
    }

    @Override
    public void showShortToast(String message) {
        super.showShortToast(message);
    }

    @Override
    public void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message) {
        super.showProgressBar(isTouchOutside, isCancel, title, message);
    }

    @Override
    public void closeProgressBar() {
        super.closeProgressBar();
    }

    @Override
    public void isParkingMaster() {
        setParkingMaster();
    }

    @Override
    public void onActiveMasterSuccess() {
        setParkingMaster();
    }

    @Override
    public void onActiveMasterFailed(String error) {
        showShortToast(error);
    }

    @Override
    public void onUserDataUpdate(String avatar, String name) {
        if (avatar != null && !avatar.isEmpty()) {
            Picasso.with(HomeActivity.this)
                    .load(avatar)
                    .noPlaceholder()
                    .error(R.mipmap.ic_launcher)
                    .into(img_avatar);
        } else
            img_avatar.setImageResource(R.mipmap.ic_user);

        txt_name.setText(name);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_parking_home) {
            replaceFragment(R.id.home_layout_content, new HomeFragment(), HOME_FRAGMENT);

            if (menu != null) {
                menu.findItem(R.id.nav_parking_add).setVisible(false);
                menu.findItem(R.id.nav_parking_checkin).setVisible(true);
            }
        } else if (id == R.id.nav_parking_quanlytaikhoan) {
            replaceFragment(R.id.home_layout_content, new ParkingManagementFragment(), MANAGER_FRAGMENT);

            if (menu != null) {
                menu.findItem(R.id.nav_parking_add).setVisible(true);
                menu.findItem(R.id.nav_parking_checkin).setVisible(false);
            }
        } else if (id == R.id.nav_parking_favorite) {
            HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
            if (fragment != null && fragment.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                fragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            startActivityForResult(FavoriteActivity.class, REQUEST_CODE);
        } else if (id == R.id.nav_parking_dangxuat) {
            LoginManager.getInstance().logOut();
            SharedPreferencesUtils.getInstance().clearData();
            startActivityAndFinish(MainActivity.class);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_parking_checkin) {
            startActivity(CheckInActivity.class);
        } else if (id == R.id.nav_parking_add) {
            startActivityForResult(AddParkingActivity.class, Constants.ADD_PARKING_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.header_img_avatar) {
            startActivity(ProfileActivity.class);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.home_btn_active) {
            homePresenter.activeParkingMaster();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (img_avatar != null && txt_name != null)
            homePresenter.updateUserData();
    }

    @Override
    public void onBackPressed() {
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
        if (fragment != null && fragment.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            fragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showConfirmExitApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_GUID) {
            replaceFragment(R.id.home_layout_content, new HomeFragment(), HOME_FRAGMENT);
            PARKING_ID = data.getIntExtra(Constants.ID_PARKING, -1);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MANAGER_FRAGMENT);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}