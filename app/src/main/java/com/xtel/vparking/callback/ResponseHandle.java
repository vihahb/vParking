package com.xtel.vparking.callback;


import android.util.Log;

import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Basic;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;

import java.io.IOException;

/**
 * Created by Mr. M.2 on 12/4/2016.
 */

public abstract class ResponseHandle<T extends RESP_Basic> {
    private Class<T> clazz;

    protected ResponseHandle(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void onSuccess(String result) {
        Log.e(this.getClass().getSimpleName(), "Result " + result);
        try {
            boolean isJson;
            isJson = !(result == null || result.isEmpty());

            Log.e(this.getClass().getSimpleName(), "Is Json: " + isJson);
            if (!isJson) {
                Log.e(this.getClass().getSimpleName(), "Success null");
                onSuccess((T) new RESP_Parking_Info());
            } else {
                T t = JsonHelper.getObjectNoException(result, clazz);
                if (t.getError() != null) {
                    onError(t.getError());
                    Log.e(this.getClass().getSimpleName(), "Error");
                } else {
                    onSuccess(t);
                    Log.e(this.getClass().getSimpleName(), "Success");
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error parse: " + e.getMessage());
            onError(new Error(-1, "ERROR_PARSER_RESPONSE", e.getMessage()));
        }
    }

    public void onError(IOException error) {
        Log.e(this.getClass().getSimpleName(), "Error: " + error.getMessage());
        onError(new Error(-1, "ERROR_PARSER_RESPONSE", error.getMessage()));
    }

    public abstract void onSuccess(T obj);

    public abstract void onError(Error error);
}
