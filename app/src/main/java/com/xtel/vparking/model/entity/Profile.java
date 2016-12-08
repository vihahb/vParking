package com.xtel.vparking.model.entity;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.BasicModel;
import com.xtel.vparking.utils.SharedPreferencesUtils;

/**
 * Created by vivhp on 12/8/2016.
 */

public class Profile extends BasicModel{
    public static Profile instance = new Profile();
    public static Profile getInstance(){
        return instance;
    }

    public UserModel gettingUserFromShared(){
        String avatar = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_AVATAR);
        String fullname = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_FULL_NAME);
        String email = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_EMAIL);
        String birthday = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_BIRTH_DAY);
        int gender = SharedPreferencesUtils.getInstance().getIntValue(Constants.USER_GENDER);
        String phone = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_PHONE);
        String qr_code = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_QR);
        String bar_code = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_BAR);

        UserModel userModel = new UserModel(fullname, gender, birthday, email, phone, avatar, qr_code, bar_code);
        return userModel;
    }

    public void updateUser(String url, String jsonObject, String session, ResponseHandle respUserResponseHandle){
        requestServer.postApi(url, jsonObject, session, respUserResponseHandle);
    }
}
