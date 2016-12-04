package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by Computer on 11/9/2016.
 */

public class Error {
    @Expose
    private int code;
    @Expose
    private String type;
    @Expose
    private String message;

    public Error() {
    }

    public Error(int code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
