package com.xtel.vparking.presenter;

import com.xtel.vparking.callback.ICmd;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.view.MyApplication;

/**
 * Created by Mr. M.2 on 12/19/2016.
 */

public abstract class BasicPresenter {

    protected void getNewSession(final ICmd cmd) {
        GetNewSession.getNewSession(MyApplication.context, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
               cmd.execute();
            }

            @Override
            public void onError() {
                getSessionError();
            }
        });
    }

    public abstract void getSessionError();
}
