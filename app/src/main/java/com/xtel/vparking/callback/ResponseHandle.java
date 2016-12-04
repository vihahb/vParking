package com.xtel.vparking.callback;


import com.google.gson.reflect.TypeToken;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Basic;
import com.xtel.vparking.utils.JsonHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/4/2016.
 */

public abstract class ResponseHandle<T extends RESP_Basic> {
    private Class<T> clazz;

    protected ResponseHandle(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void onSuccess(String result) {
        try {
            T t = JsonHelper.getObjectNoException(result, clazz);
            if (t.getError() != null)
                onError(t.getError());
            else
                onSuccess(t);
        } catch (Exception e) {
            onError(new Error(-1, "ERROR_PARSER_RESPONSE", e.getMessage()));
        }
    }

    public void onError(IOException error) {
        onError(new Error(-1, "ERROR_PARSER_RESPONSE", error.getMessage()));
    }

    public abstract void onSuccess(T obj);

    public abstract void onError(Error error);
}
