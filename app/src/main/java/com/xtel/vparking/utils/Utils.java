package com.xtel.vparking.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.xtel.vparking.R;

/**
 * Created by Lê Công Long Vũ on 12/21/2016.
 */

public class Utils {
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}
