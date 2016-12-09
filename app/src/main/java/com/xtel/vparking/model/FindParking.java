package com.xtel.vparking.model;

import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.model.entity.Find;

/**
 * Created by vivhp on 12/9/2016.
 */

public class FindParking extends BasicModel {

    public static FindParking instance = new FindParking();

    public static FindParking getInstance(){
        return instance;
    }
}
