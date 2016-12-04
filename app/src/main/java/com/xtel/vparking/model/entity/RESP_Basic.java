package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class RESP_Basic {
    @Expose
    private Error error;

    public RESP_Basic() {
    }

    public RESP_Basic(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "RESP_Basic{" +
                "error=" + error +
                '}';
    }
}
