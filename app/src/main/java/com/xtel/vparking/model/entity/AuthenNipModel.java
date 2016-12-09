package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by vivhp on 12/9/2016.
 */

public class AuthenNipModel {
    @Expose
    private String access_token_key;
    @Expose
    private String authorization_code;
    @Expose
    private String service_code = "PRK";
    @Expose
    private DeviceObject devInfo;

    public String getAccess_token_key() {
        return access_token_key;
    }

    public void setAccess_token_key(String access_token_key) {
        this.access_token_key = access_token_key;
    }

    public String getAuthorization_code() {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code) {
        this.authorization_code = authorization_code;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public DeviceObject getDevInfo() {
        return devInfo;
    }

    public void setDevInfo(DeviceObject devInfo) {
        this.devInfo = devInfo;
    }
}
