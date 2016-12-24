package com.xtel.vparking.model.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/24/2016.
 */

public class RESP_Address extends RESP_Basic {
    @Expose
    private ArrayList<Address> results;
    @Expose
    private String status;

    public ArrayList<Address> getResults() {
        return results;
    }

    public void setResults(ArrayList<Address> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Address {
        @Expose
        private String formatted_address;

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }
    }
}
