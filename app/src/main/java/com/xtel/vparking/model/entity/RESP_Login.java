package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by vivhp on 12/8/2016.
 */

public class RESP_Login extends RESP_Basic {
    @Expose
    public String authenticationid;
    @Expose
    public String session;
    @Expose
    public long login_time;
    @Expose
    public long expired_time;

    public String getAuthenticationid() {
        return authenticationid;
    }

    public void setAuthenticationid(String authenticationid) {
        this.authenticationid = authenticationid;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public long getLogin_time() {
        return login_time;
    }

    public void setLogin_time(long login_time) {
        this.login_time = login_time;
    }

    public long getExpired_time() {
        return expired_time;
    }

    public void setExpired_time(long expired_time) {
        this.expired_time = expired_time;
    }

    @Override
    public String toString() {
        return "Login{" +
                "authenticationid='" + authenticationid + '\'' +
                ", session='" + session + '\'' +
                ", login_time=" + login_time +
                ", expired_time=" + expired_time +
                '}';
    }


}
