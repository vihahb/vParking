package com.xtel.vparking.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xtel.vparking.R;
import com.xtel.vparking.view.MyApplication;
import com.xtel.vparking.view.fragment.ViewCheckInFragment;
import com.xtel.vparking.view.fragment.ViewHistoryFragment;

/**
 * Created by Lê Công Long Vũ on 12/19/2016.
 */

public class ViewParkingAdapter extends FragmentStatePagerAdapter {
    private String[] list_title;
    private int id;

    public ViewParkingAdapter(FragmentManager fm, int id) {
        super(fm);
        list_title = MyApplication.context.getResources().getStringArray(R.array.view_parking_title);
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ViewCheckInFragment.newInstance(id);
            case 1:
                return ViewHistoryFragment.newInstance(id);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return list_title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list_title[position];
    }
}
