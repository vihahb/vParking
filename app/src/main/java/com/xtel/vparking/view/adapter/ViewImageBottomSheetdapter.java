package com.xtel.vparking.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xtel.vparking.view.fragment.ViewImageBottomSheetFragment;

import java.util.ArrayList;

/**
 * Created by Computer on 11/10/2016.
 */

public class ViewImageBottomSheetdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> arrayList;

    public ViewImageBottomSheetdapter(FragmentManager fm, ArrayList<String> arrayList) {
        super(fm);
        this.arrayList = arrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return ViewImageBottomSheetFragment.newInstance(arrayList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
