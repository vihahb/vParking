package com.xtel.vparking.commons;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Login;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.SharedPreferencesUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by vivhp on 11/21/2016.
 */

public class GetNewSession {

    public static void getNewSession(final Context context, final RequestNoResultListener requestNoResultListener) {
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


        authentication_id = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_AUTH_ID);
        service_code = "PRK";

        JsonObject userAuthentJson = new JsonObject();
        JsonObject deviceObject = new JsonObject();
        deviceObject.addProperty(Constants.DEVICE_ID, device_id);
        deviceObject.addProperty(Constants.DEVICE_OS_NAME, device_os_name);
        deviceObject.addProperty(Constants.DEVICE_OS_VER, device_os_ver);
        deviceObject.addProperty(Constants.DEVICE_OTHER, other);
        deviceObject.addProperty(Constants.DEVICE_TYPE, device_type);
        deviceObject.addProperty(Constants.DEVICE_VENDOR, device_vendor);

        userAuthentJson.addProperty("authenticationid", authentication_id);
        userAuthentJson.addProperty("service_code", service_code);
        userAuthentJson.add("devInfo", deviceObject);

        Log.d("Authen Object: ", authentication_id);
        Log.d("Code Object: ", service_code);
        Log.d("Device Object: ", deviceObject.toString());
        Log.d("User object: ", String.valueOf(userAuthentJson));

        String url_authen = Constants.SERVER_AUTHEN + Constants.AUTHEN_AUTHENTICATE;

        LoginModel.getInstance().getNewSession(url_authen, userAuthentJson.toString(), new ResponseHandle<RESP_Login>(RESP_Login.class) {
            @Override
            public void onSuccess(RESP_Login obj) {
                Log.v("New session: ", obj.getSession());
                String newSession = obj.getSession();
                LoginModel.getInstance().postingNewSession(newSession);
                requestNoResultListener.onSuccess();
                checkTime(obj.getLogin_time(), obj.getExpired_time());
            }

            @Override
            public void onError(Error error) {
                Log.e("Ma loi get session: ", String.valueOf(error.getCode()));
                Log.e("Message get session: ", error.getMessage());
                requestNoResultListener.onError();
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

    private static void checkTime(long login_time, long expired_time){
        String time = "login: " + convertLong2Time(login_time) + ", Expired: " + convertLong2Time(expired_time);
        Log.v("Time new session: ", time);
    }
}
