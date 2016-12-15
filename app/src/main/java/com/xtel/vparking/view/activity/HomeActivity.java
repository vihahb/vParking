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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.xtel.vparking.view.fragment.FavoriteFragment;
import com.xtel.vparking.view.fragment.HomeFragment;
import com.xtel.vparking.view.fragment.ManagementFragment;
import com.xtel.vparking.view.fragment.CheckedFragment;
import com.xtel.vparking.view.fragment.VerhicleFragment;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class HomeActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        HomeView {
    public static HomeActivity instance;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView img_avatar;
    private TextView txt_name;
    private Button btn_active_master;
    private Menu menu;
    private HomePresenter homePresenter;
    private ActionBar actionBar;

    private final String HOME_FRAGMENT = "home_fragment", MANAGER_FRAGMENT = "manager_fragment", VERHICLE_FRAGMENT = "verhicle_fragment",
            FAVORITE_FRAGMENT = "favorite_fragment", CHECKIN_FRAGMENT = "checkin_fragment";
    private String CURRENT_FRAGMENT = "";
    public static final int REQUEST_CODE = 99;
    public static final int RESULT_GUID = 88;
    public static int PARKING_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        replaceFragment(R.id.home_layout_content, new HomeFragment(), HOME_FRAGMENT);
        CURRENT_FRAGMENT = HOME_FRAGMENT;

        initView();
        initNavigation();
        initListener();
        homePresenter = new HomePresenter(this);
        instance = this;
    }

    public static HomeActivity getInstance() {
        return instance;
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

        actionBar = getSupportActionBar();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initListener() {
        navigationView.setNavigationItemSelectedListener(this);
        img_avatar.setOnClickListener(this);
        btn_active_master.setOnClickListener(this);
    }

    private void setParkingMaster() {
        navigationView.getMenu().findItem(R.id.nav_parking_management).setVisible(true);
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

    public void viewParkingSelected(int id) {
        replaceHomeFragment();
        PARKING_ID = id;
    }

    private void replaceHomeFragment() {
        replaceFragment(R.id.home_layout_content, new HomeFragment(), HOME_FRAGMENT);
        CURRENT_FRAGMENT = HOME_FRAGMENT;

        actionBar.setTitle(getString(R.string.title_activity_home));
        if (menu != null) {
            menu.findItem(R.id.nav_parking_add).setVisible(false);
            menu.findItem(R.id.nav_parking_checkin).setVisible(true);
        }
    }

    private void replaceManagementFragment() {
        replaceFragment(R.id.home_layout_content, new ManagementFragment(), MANAGER_FRAGMENT);
        CURRENT_FRAGMENT = MANAGER_FRAGMENT;

        actionBar.setTitle(getString(R.string.title_activity_management));
        if (menu != null) {
            menu.findItem(R.id.nav_parking_add).setVisible(true);
            menu.findItem(R.id.nav_parking_checkin).setVisible(false);
        }
    }

    private void replaceFavoriteFragment() {
        replaceFragment(R.id.home_layout_content, new FavoriteFragment(), FAVORITE_FRAGMENT);
        CURRENT_FRAGMENT = FAVORITE_FRAGMENT;

        actionBar.setTitle(getString(R.string.title_activity_favorite));
        if (menu != null) {
            menu.findItem(R.id.nav_parking_add).setVisible(false);
            menu.findItem(R.id.nav_parking_checkin).setVisible(false);
        }
    }

    private void replaceVerhicleFragment() {
        replaceFragment(R.id.home_layout_content, new VerhicleFragment(), VERHICLE_FRAGMENT);
        CURRENT_FRAGMENT = VERHICLE_FRAGMENT;

        actionBar.setTitle(getString(R.string.title_activity_verhicle));
        if (menu != null) {
            menu.findItem(R.id.nav_parking_add).setVisible(true);
            menu.findItem(R.id.nav_parking_checkin).setVisible(false);
        }
    }

    private void replaceCheckInFragment() {
        replaceFragment(R.id.home_layout_content, new CheckedFragment(), CHECKIN_FRAGMENT);
        CURRENT_FRAGMENT = CHECKIN_FRAGMENT;

        actionBar.setTitle(getString(R.string.title_activity_check_in));
        if (menu != null) {
            menu.findItem(R.id.nav_parking_add).setVisible(false);
            menu.findItem(R.id.nav_parking_checkin).setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_parking_home) {
            replaceHomeFragment();
        } else if (id == R.id.nav_parking_management) {
            replaceManagementFragment();
        } else if (id == R.id.nav_parking_favorite) {
            replaceFavoriteFragment();
        } else if (id == R.id.nav_parking_verhicle) {
            replaceVerhicleFragment();
        } else if (id == R.id.nav_parking_checkin) {
            replaceCheckInFragment();
        } else if (id == R.id.nav_parking_logout) {
            LoginManager.getInstance().logOut();
            SharedPreferencesUtils.getInstance().clearData();
            startActivityAndFinish(LoginActivity.class);
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
            if (CURRENT_FRAGMENT.equals(MANAGER_FRAGMENT))
                startActivityForResult(AddParkingActivity.class, Constants.ADD_PARKING_REQUEST);
            else if (CURRENT_FRAGMENT.equals(VERHICLE_FRAGMENT))
                startActivityForResult(AddVerhicleActivity.class, VerhicleFragment.REQUEST_ADD_VERHICLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.header_img_avatar) {
            startActivity(ProfileActivitys.class);
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
        debug("clicked");
        if (HomeFragment.bottomSheetBehavior != null && HomeFragment.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            HomeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showConfirmExitApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e(this.getClass().getSimpleName(), "request " + requestCode + " result " + resultCode);
        if (CURRENT_FRAGMENT.equals(HOME_FRAGMENT)) {
            Log.e(this.getClass().getSimpleName(), "request " + requestCode + " result " + resultCode);
            Fragment fragment0 = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
            if (fragment0 != null) {
                Log.e(this.getClass().getSimpleName(), "request " + requestCode + " result " + resultCode);
                fragment0.onActivityResult(requestCode, resultCode, data);
            }
        } else if (CURRENT_FRAGMENT.equals(VERHICLE_FRAGMENT)) {
            Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(VERHICLE_FRAGMENT);
            if (fragment1 != null) {
                fragment1.onActivityResult(requestCode, resultCode, data);
            }
        } else if (CURRENT_FRAGMENT.equals(MANAGER_FRAGMENT)) {
            Fragment fragment2 = getSupportFragmentManager().findFragmentByTag(MANAGER_FRAGMENT);
            if (fragment2 != null) {
                fragment2.onActivityResult(requestCode, resultCode, data);
            }
        } else if (CURRENT_FRAGMENT.equals(CHECKIN_FRAGMENT)) {
            Fragment fragment3 = getSupportFragmentManager().findFragmentByTag(CHECKIN_FRAGMENT);
            if (fragment3 != null) {
                fragment3.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}