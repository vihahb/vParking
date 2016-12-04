package com.xtel.vparking.model;

import android.os.AsyncTask;

import com.xtel.vparking.callback.RequestServer;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.utils.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mr. M.2 on 12/2/2016.
 */

public abstract class BasicModel {
    protected RequestServer requestServer = new RequestServer();

    protected String getNIPUMApiUrlBase() {
        return Constants.SERVER_AUTHEN;
    }

    protected String getParkingApiUrlBase() {
        return Constants.SERVER_PARKING;
    }
}
