package com.xtel.vparking.view.activity.inf;

import android.app.Activity;
import android.content.Intent;

import com.xtel.vparking.model.entity.UserModel;

/**
 * Created by vivhp on 12/8/2016.
 */

public interface ProfileView extends IView {
    void onUpdateSuccess();
    void onUpdateError();

    void onGetDataSuccess(UserModel userModel);

    @Override
    void showShortToast(String message);

    @Override
    void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message);

    @Override
    void closeProgressBar();

    void startActivityForResult(Intent intent, int requestCode);

    void updatePhone(String phone);

    Activity getActivity();
}
