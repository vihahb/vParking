package com.xtel.vparking.commons;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import vn.xtel.quanlybaido.callback.GetNewSessionListener;
import vn.xtel.quanlybaido.data.AppPreferences;
import vn.xtel.quanlybaido.data.JsonParse;
import vn.xtel.quanlybaido.model.ErrorModel;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by vivhp on 11/21/2016.
 */

public class GetNewSession {

    public static void getNewSession(final Context context, final GetNewSessionListener getNewSessionListener) {
        String device_id;
        String device_os_name;
        String device_os_ver;
        String other;
        int device_type;
        String device_vendor;

        String authentication_id;
        String service_code;

        //Get Device Info
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        //Getting device ID
        //get Inf
        device_id = telephonyManager.getDeviceId();
        device_os_name = android.os.Build.VERSION.CODENAME;
        device_os_ver = android.os.Build.VERSION.RELEASE;
        other = "chua co gi ca";
        device_type = 1;
        device_vendor = android.os.Build.MANUFACTURER;
        Log.e("Device info: ", "Name: " + device_vendor + ", Android name: " + device_os_name + ", version: " + device_os_ver + ", id: " + device_id);


        final AppPreferences appPreferences = new AppPreferences(context);
        appPreferences.prepair();

        authentication_id = appPreferences.getStringValue(Constants.USER_AUTH_ID);
        service_code = "PRK";

        JsonObject userAuthentJson = new JsonObject();
        JsonObject deviceObject = new JsonObject();
        deviceObject.addProperty(Constants.DEVICE_ID, device_id);
        deviceObject.addProperty(Constants.DEVICE_OS_NAME, device_os_name);
        deviceObject.addProperty(Constants.DEVICE_OS_VER, device_os_ver);
        deviceObject.addProperty(Constants.DEVICE_OTHER, other);
        deviceObject.addProperty(Constants.DEVICE_TYPE, device_type);
        deviceObject.addProperty(Constants.DEVICE_VENDOR, device_vendor);

        Log.d("Authen Object: ", authentication_id);
        Log.d("Code Object: ", service_code);
        Log.d("Device Object: ", deviceObject.toString());

        userAuthentJson.addProperty("authenticationid", authentication_id);
        userAuthentJson.addProperty("service_code", service_code);
        userAuthentJson.add("devInfo", deviceObject);

        Log.d("User object: ", String.valueOf(userAuthentJson));


        Ion.with(context)
                .load(Constants.SERVER_AUTHEN + Constants.AUTHEN_AUTHENTICATE)
                .setJsonObjectBody(userAuthentJson)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.e("Co loi: roi", e.toString());
                            Toast.makeText(context, "Co loi xay ra", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("OK: ", result.toString());
                            ErrorModel errorModel = JsonParse.checkError(result.toString());
                            if (errorModel != null) {
                                Log.e("Ma loi: ", String.valueOf(errorModel.getCode()));
                                getNewSessionListener.onError();
                            } else {
                                Log.e("result: ", result.toString());
                                String session = result.get("session").getAsString();
                                long login_time = result.get("login_time").getAsLong();
                                long expired_time = result.get("expired_time").getAsLong();
                                appPreferences.putStringValue(Constants.USER_SESSION, session);
                                getNewSessionListener.onSuccess();
                                String LoginTime = convertLong2Time(login_time);
                                String ExpiredTime = convertLong2Time(expired_time);
                                String TimeSession = "Login Time: " + LoginTime + ", Expired Time: " + ExpiredTime;
                                Log.e("Time for session:", TimeSession);
                            }
                        }

                    }
                });
    }

    public static String convertLong2Time(long time) {
        Date date = new Timestamp(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        String formatTime = dateFormat.format(date);
        return formatTime;
    }
}
