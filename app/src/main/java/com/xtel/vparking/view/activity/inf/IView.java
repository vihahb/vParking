package com.xtel.vparking.view.activity.inf;

/**
 * Created by Mr. M.2 on 12/6/2016.
 */

public interface IView {

    void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message);
    void closeProgressBar();
    void showShortToast(String message);
    void showLongToast(String message);
}
