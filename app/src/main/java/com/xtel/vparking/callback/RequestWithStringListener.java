package com.xtel.vparking.callback;

/**
 * Created by Lê Công Long Vũ on 12/1/2016.
 */

public interface RequestWithStringListener {
    void onSuccess(String url);
    void onError();
}