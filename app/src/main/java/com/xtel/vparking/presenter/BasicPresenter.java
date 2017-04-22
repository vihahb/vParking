package com.xtel.vparking.presenter;

import android.app.Activity;

import com.xtel.vparking.callback.ICmd;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.view.MyApplication;

/**
 * Created by Mr. Vulcl on 12/19/2016
 */

public abstract class BasicPresenter {

    protected void getNewSession(Activity activity, final ICmd cmd, final Object... objects) {
        GetNewSession.getNewSession(activity, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
               cmd.execute(objects);
            }

            @Override
            public void onError() {
                getSessionError();
            }
        });
    }

    public abstract void getSessionError();
}
