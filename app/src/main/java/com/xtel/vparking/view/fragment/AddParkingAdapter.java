package com.xtel.vparking.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xtel.vparking.model.entity.Pictures;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/9/2016.
 */

public class AddParkingAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Pictures> arrayList;

    public AddParkingAdapter(FragmentManager fm, ArrayList<Pictures> arrayList) {
        super(fm);
        this.arrayList = arrayList;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return ViewImageFragment.newInstance(arrayList.get(position).getUrl());
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
