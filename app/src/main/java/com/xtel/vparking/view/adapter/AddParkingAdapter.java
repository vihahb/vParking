package com.xtel.vparking.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xtel.vparking.view.fragment.ImageItemFragment;

import java.util.ArrayList;

/**
 * Created by Computer on 11/10/2016.
 */

public class AddParkingAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> fragments;

    public AddParkingAdapter(FragmentManager fm, ArrayList<String> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageItemFragment.newInstance(fragments.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
//        ImageItemFragment f = (ImageItemFragment) object;
//        if (f != null) {
//            f.update();
//        }
        return POSITION_NONE;
//        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
