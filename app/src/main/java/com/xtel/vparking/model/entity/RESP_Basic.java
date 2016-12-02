package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import vn.xtel.quanlybaido.model.ErrorModel;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class RESP_Basic {
    @Expose
    private ErrorModel error;

    public RESP_Basic(ErrorModel error) {
        this.error = error;
    }

    public ErrorModel getError() {
        return error;
    }

    public void setError(ErrorModel error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "RESP_Basic{" +
                "error=" + error +
                '}';
    }
}
