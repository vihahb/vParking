package com.xtel.vparking.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking;
import com.xtel.vparking.view.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 11/9/2016.
 */

public class JsonParse {

    public static Error checkError(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject error = jsonObject.getJSONObject(Constants.JSON_ERROR);

            Error errorModel = new Error();
            errorModel.setCode(error.getInt(Constants.JSON_CODE));
            errorModel.setType(error.getString(Constants.JSON_TYPE));
            errorModel.setMessage(error.getString(Constants.JSON_MESSAGE));

            return errorModel;
        } catch (Exception e) {
            Log.e("parse_error", e.toString());
            e.printStackTrace();
        }

        return null;
    }

//    public static ArrayList<RESP_Parking> getAllParking(String data) {
//        try {
//            JSONArray jsonArray = new JSONArray(data);
//            ArrayList<RESP_Parking> arrayList = new ArrayList<>();
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject parking = jsonArray.getJSONObject(i);
//
//                RESP_Parking RESPRESPParking = new RESP_Parking();
//                RESPRESPParking.setId(parking.getInt(Constants.JSON_ID));
//                RESPRESPParking.setUid(parking.getInt(Constants.JSON_UID));
//                RESPRESPParking.setLat(parking.getDouble(Constants.JSON_LAT));
//                RESPRESPParking.setLng(parking.getDouble(Constants.JSON_LNG));
//                RESPRESPParking.setType(parking.getDouble(Constants.JSON_TYPE));
//                RESPRESPParking.setStatus(parking.getDouble(Constants.JSON_STATUS));
//                RESPRESPParking.setCode(parking.getString(Constants.JSON_CODE));
//                RESPRESPParking.setBegin_time(parking.getString(Constants.JSON_BEGIN_TIME));
//                RESPRESPParking.setEnd_time(parking.getString(Constants.JSON_END_TIME));
//                RESPRESPParking.setAddress(parking.getString(Constants.JSON_ADDRESS));
//
//                arrayList.add(RESPRESPParking);
//            }
//
//            return arrayList;
//        } catch (Exception e) {
//            Log.e("pk_json_loi", e.toString());
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static void getCodeError(Context activity, View view, int code, String content) {
        if (code == 3) {
            if (view != null)
                Snackbar.make(view, activity.getString(R.string.error_no_permission), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.error_no_permission), Toast.LENGTH_SHORT).show();
        } else if (code == 4) {
            if (view != null)
                Snackbar.make(view, activity.getString(R.string.error_system), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.error_system), Toast.LENGTH_SHORT).show();
        } else if (code == 1001) {
            if (view != null)
                Snackbar.make(view, activity.getString(R.string.error_no_parking), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.error_no_parking), Toast.LENGTH_SHORT).show();
        } else {
            if (view != null)
                Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getCodeMessage(int code, String content) {
        if (code == 2) {
            return  MyApplication.context.getString(R.string.error_session_invalid);
        } else if (code == 3) {
            return MyApplication.context.getString(R.string.error_no_permission);
        } else if (code == 4) {
            return MyApplication.context.getString(R.string.error_system);
        } else if (code == 1001) {
            return MyApplication.context.getString(R.string.error_no_parking);
        } else {
            return content;
        }
    }
}