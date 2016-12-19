package com.xtel.vparking.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.xtel.vparking.commons.Constants;

/**
 * Created by Mr. M.2 on 12/19/2016.
 */

public class ViewHistoryFragment extends Fragment {

    public static ViewHistoryFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(Constants.ID_PARKING, id);
        ViewHistoryFragment fragment = new ViewHistoryFragment();
        fragment.setArguments(args);

        Log.e("vp", "id history fragment " + id);

        return fragment;
    }
}
