package com.xtel.vparking.model;

import com.xtel.vparking.commons.Constants;
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
}
