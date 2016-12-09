package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.RESP_FLAG;
import com.xtel.vparking.model.entity.RESP_Login;
import com.xtel.vparking.model.entity.RESP_User;
import com.xtel.vparking.utils.SharedPreferencesUtils;

/**
 * Created by vivhp on 12/7/2016.
 */

public class LoginModel extends BasicModel {
    public static LoginModel instance = new LoginModel();

    public static LoginModel getInstance() {
        return instance;
    }

    public String getSession() {
        return SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
    }

    public void getNewSession(String url, String jsonObject, ResponseHandle<RESP_Login> responseHandle){
        requestServer.postApi(url, jsonObject, null, responseHandle);
    }

    public void postingNewSession(String newSession){
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_SESSION, newSession);
    }

    public void postFbData2Server(String url, String jsonObject, ResponseHandle<RESP_Login> responseHandle){
        requestServer.postApi(url, jsonObject, null, responseHandle);
    }

    public void postAccountKitData2Server(String url, String jsonObject, ResponseHandle<RESP_Login> responseHandle){
        requestServer.postApi(url, jsonObject, null, responseHandle);
    }

    public void savingData2Shared(String authentication_id, String session, long login_time, long expired_time){
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AUTH_ID, authentication_id);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_SESSION, session);
        SharedPreferencesUtils.getInstance().putLongValue(Constants.USER_LOGIN_TIME, login_time);
        SharedPreferencesUtils.getInstance().putLongValue(Constants.USER_EXPIRED_TIME, expired_time);
    }

    public void gettingFlag(String url, String session, ResponseHandle<RESP_FLAG> flagResponseHandle){
        requestServer.getApi(url, session, flagResponseHandle);
    }

    public void postingFlag2Shared(int parking_flag){
        SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_FLAG, parking_flag);
    }

    public void gettingUser(String url, String session, ResponseHandle<RESP_User> userResponseHandle){
        requestServer.getApi(url, session, userResponseHandle);
    }

    public void postingUser2Shared(String fullname, int gender, String birthday, String email, String phone, String avatar, String qr_code, String bar_code){
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_FULL_NAME, fullname);
        SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_GENDER, gender);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_BIRTH_DAY, birthday);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_EMAIL, email);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_PHONE, phone);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AVATAR, avatar);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_QR, qr_code);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_BAR, bar_code);
    }
}
