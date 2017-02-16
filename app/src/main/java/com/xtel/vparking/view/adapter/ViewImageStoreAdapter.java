package com.xtel.vparking.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xtel.vparking.view.fragment.ViewImageFragment;
import com.xtel.vparking.view.fragment.ViewImageStoreFragment;

import java.util.ArrayList;

/**
 * Created by Computer on 11/10/2016
 */

public class ViewImageStoreAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> fragments;

    public ViewImageStoreAdapter(FragmentManager fm, ArrayList<String> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return ViewImageStoreFragment.newInstance(fragments, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
