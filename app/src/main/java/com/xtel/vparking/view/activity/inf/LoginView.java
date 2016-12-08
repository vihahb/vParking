package com.xtel.vparking.view.activity.inf;

import android.app.Activity;
import android.content.Intent;

import com.xtel.vparking.model.entity.Error;

/**
 * Created by vivhp on 12/8/2016.
 */

public interface LoginView extends IView {

    void onLoginSuccess();
    void onLoginErrors(Error error);

    @Override
    void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message);

    @Override
    void showShortToast(String message);

    @Override
    void showLongToast(String message);

    void startActivity(Class clazz);
    void startActivityAndFinish(Class clazz);

    void startActivityForResult(Intent intent, int requestCode);

    Activity getActivity();

}
