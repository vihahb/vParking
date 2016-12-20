package com.xtel.vparking.view.widget;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Lê Công Long Vũ on 12/20/2016.
 */

public class NoPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        if (position <= -1.0F) {
            view.setAlpha(0);
        } else if (position < 0F) {
            view.setAlpha(1);
            view.setTranslationX((int) ((float) (view.getWidth()) * -position));
        } else if (position >= 0F) {
            view.setAlpha(1);
        } else if (position > 1.0F) {
            view.setAlpha(0);
        }
    }
}