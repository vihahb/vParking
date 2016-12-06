package com.xtel.vparking.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.dialog.DialogProgressBar;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.UserModel;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private final int MY_REQUEST_CODE = 1001;
    CallbackManager callbackManager;
    AccessTokenTracker tokenTracker;
    AccessToken accessToken;
    String session;

    //User param
    String authorization_code;
    int parkingFlag;

    boolean check_acc_kit;

    String user_token, device_id, device_os_name, device_os_ver, other = "Chưa có", device_vendor;
    //    TelephonyManager telephonyManager;
    int device_type;
    boolean isWaitingForExit = false;
    private Button btn_signin, btn_signup, btn_Facebook_login;
    private int MY_CODE_RESULT = 1001;
    DialogProgressBar progressBar;
    public static int ACC_REQUEST_CODE = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                checkAccessToken();
                Log.e("Access Token:", loginResult.getAccessToken().getToken());
                user_token = loginResult.getAccessToken().getToken();
                initAccoutFb();
                Log.d("User ID: ", loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Login Cancel! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getBaseContext(), "Login Error! \n" + error, Toast.LENGTH_SHORT).show();
            }
        });

        initWidget();
        initOnClickButton();


        initKeyHash();
        CheckPermission();
    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)
                    && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)
                    && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS)){

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS},
                        MY_REQUEST_CODE);
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS},
                        MY_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            getDeviceInfo();
        }
    }

    private void initAccoutFb() {
        progressBar = new DialogProgressBar(MainActivity.this, false, false, null, getString(R.string.parking_get_data));
        progressBar.showProgressBar();

        getDeviceInfo();

        if (device_id != null && device_os_name != null
                && device_os_ver != null && other != null
                && device_type != 0 && device_vendor != null) {
            String service_code = "PRK";

            JsonObject userJson = new JsonObject();
            JsonObject deviceObject = new JsonObject();
            deviceObject.addProperty(Constants.DEVICE_ID, device_id);
            deviceObject.addProperty(Constants.DEVICE_OS_NAME, device_os_name);
            deviceObject.addProperty(Constants.DEVICE_OS_VER, device_os_ver);
            deviceObject.addProperty(Constants.DEVICE_OTHER, other);
            deviceObject.addProperty(Constants.DEVICE_TYPE, device_type);
            deviceObject.addProperty(Constants.DEVICE_VENDOR, device_vendor);

            userJson.addProperty("access_token_key", user_token);
            userJson.addProperty("service_code", service_code);
            userJson.add("devInfo", deviceObject);

            Log.e("Test Json Object: ", user_token);
            Log.e("Test Json Object: ", service_code);
            Log.e("Test device Object: ", String.valueOf(deviceObject));

            Ion.with(MainActivity.this)
                    .load(Constants.SERVER_AUTHEN + Constants.AUTHEN_FB_LOGIN)
                    .setJsonObjectBody(userJson)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                Log.e("Exception:", e.toString());
                            } else {
                                Log.e("Result: ", result.toString());
                                Error errorModel = JsonParse.checkError(result.toString());
                                if (errorModel != null) {
                                    Log.e("Ma loi: ", String.valueOf(errorModel.getCode()));
                                } else {
                                    Log.e("ma_result:", result.toString());

                                    String authenticationid = result.get("authenticationid").getAsString();
                                    session = result.get("session").getAsString();
                                    long login_time = result.get("login_time").getAsLong();
                                    long expired_time = result.get("expired_time").getAsLong();
                                    int dev_info_status = result.get("dev_info_status").getAsInt();

//                            Assign variable to AppPreferenc variable
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AUTH_ID, authenticationid);
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_SESSION, session);
                                    SharedPreferencesUtils.getInstance().putLongValue(Constants.USER_LOGIN_TIME, login_time);
                                    SharedPreferencesUtils.getInstance().putLongValue(Constants.USER_EXPIRED_TIME, expired_time);
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_DEV_INFO_STATUS, authenticationid);

                                    //Put authentication to RESP_Parking
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.DEVICE_ID, device_id);
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.DEVICE_OS_NAME, device_os_name);
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.DEVICE_OS_VER, device_os_ver);
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.DEVICE_OTHER, other);
                                    SharedPreferencesUtils.getInstance().putIntValue(Constants.DEVICE_TYPE, device_type);
                                    SharedPreferencesUtils.getInstance().putStringValue(Constants.DEVICE_VENDOR, device_vendor);

                                    String results = "Auth_ID: " + authenticationid + ", Session: " + session + ", login time: " + login_time + ", Ex Time: " + expired_time + ", Device Info status: " + dev_info_status;
                                    Log.e("Test result", results);
                                    String login_times = convertLong2Time(login_time);
                                    String expired = convertLong2Time(expired_time);
                                    String time_session = "Login time: " + login_times + ", Expired time: " + expired;
                                    Log.e("Time session: ", time_session);
                                    gettingData();
                                }
                            }
                        }
                    });
        }

    }

    //Init View Widget Control
    private void initWidget() {
        btn_signin = (Button) findViewById(R.id.btn_Signin);
        btn_Facebook_login = (Button) findViewById(R.id.btn_fb_signin);
    }
    //Set onclick button
    private void initOnClickButton() {
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginPhone(v);
            }
        });
        btn_Facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Login
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                        Arrays.asList("public_profile", "email", "user_birthday"));
            }
        });
    }

    //Check access torken
    private void checkAccessToken() {
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //Đã đăng nhập, lưu token lại và làm việc sau đăng nhập
                accessToken = currentAccessToken;
            }
        };
        //If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        Log.d("Get Access Token:", String.valueOf(AccessToken.getCurrentAccessToken().getToken()));
    }

    //Generate keyhash
    private void initKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "vn.xtelsolution.quanlybaido",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void getDeviceInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //Getting device ID
        //get Inf
        device_id = telephonyManager.getDeviceId();
        device_os_name = android.os.Build.VERSION.CODENAME;
        device_os_ver = android.os.Build.VERSION.RELEASE;
        device_type = 1;
        device_vendor = android.os.Build.MANUFACTURER;
        Log.e("Device info: ", "Name: " + device_vendor + ", Android name: " + device_os_name + ", version: " + device_os_ver + ", id: " + device_id);
    }

    private void gettingData() {
        if (session != null) {
            Log.e("url flag: ", Constants.SERVER_PARKING + Constants.GET_FLAG);
            Ion.with(MainActivity.this)
                    .load(Constants.SERVER_PARKING + Constants.GET_FLAG)
                    .setHeader(Constants.JSON_SESSION, session)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                                     @Override
                                     public void onCompleted(Exception e, JsonObject result) {

                                         if (e != null) {
                                             Log.e("Co loi:", e.toString());
                                         } else {
                                             Log.e("Result: ", result.toString());
                                             Error errorModel = JsonParse.checkError(result.toString());
                                             if (errorModel != null) {
                                                 Log.e("Loi check FLAG:", String.valueOf(errorModel.getCode()));
                                             } else {
                                                 parkingFlag = result.get("parking_flag").getAsInt();
                                                 Log.d("Flag parking: ", String.valueOf(parkingFlag));
                                                 SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_FLAG, parkingFlag);
                                             }
                                         }
                                     }
                                 }
                    );
            getJsonFromAuthen(session);
            delayStartActivity(1000);
        }


//        Ion.with(MainActivity.this)
//                .load(Constants.SERVER_AUTHEN + Constants.RET_USER + session)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        progressBar.hideProgressBar();
//                        if (e != null) {
//                            Log.e("Loi Ret USER", e.toString());
//                        } else {
//                            Log.e("Result user: ", result.toString());
//                            ErrorModel errorModel = JsonParse.checkError(result.toString());
//                            if (errorModel != null) {
//                                Log.e("Loi result:", e.toString());
//                            } else {
//                                JsonObject user_object = result.getAsJsonObject("userInfo");
//                                //Get valua from parameters
//                                first_name = user_object.get("first_name").getAsString();
//                                last_name = user_object.get("last_name").getAsString();
//                                picture = user_object.get("avatar").getAsString();
//                                gender1 = user_object.get("gender").getAsInt();
//                                email = user_object.get("email").getAsString();
////                                  phone = user_object.get("phone").getAsString();
//                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_FIRST_NAME, first_name);
//                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_LAST_NAME, last_name);
//                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AVATAR, picture);
//                                SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_GENDER, gender1);
//                                String respond = "Name: " + first_name + " " + last_name + ", Avatar: " + picture + ", gender: " + gender;
//
//                                Log.e("Profile: ", respond);
//                            }
//                        }
//                        startActivity(new Intent(MainActivity.this, ParkingActivity.class));
//                        finish();
//                    }
//                });
    }

    public void onLoginPhone(View view){
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE, AccountKitActivity.ResponseType.CODE);
        configurationBuilder.setDefaultCountryCode("VN");
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, ACC_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                Log.e("size permission:", String.valueOf(grantResults.length));
                if (grantResults.length == 6
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                    getDeviceInfo();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == ACC_REQUEST_CODE){
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toas_mes;
            if (loginResult.getError() != null){
                toas_mes = loginResult.getError().getErrorType().getMessage();
                Log.e("Loi acc kit: ", loginResult.getError().toString());
            } else if (loginResult.wasCancelled()){
                toas_mes = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null){
                    toas_mes = "Success: " + loginResult.getAccessToken().getAccountId();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    toas_mes = String.format("Success:%s...", loginResult.getAuthorizationCode().substring(0, 10));
                    Log.e("Authorization Id: ", loginResult.getAuthorizationCode().toString());
                    authorization_code = loginResult.getAuthorizationCode().toString();
                    posttingDataAccKit();
                    delayStartActivity(2000);
                }
            }
            Toast.makeText(getApplicationContext(), toas_mes, Toast.LENGTH_SHORT).show();
        }
    }

    private void posttingDataAccKit(){
        progressBar = new DialogProgressBar(MainActivity.this, false, false, null, getString(R.string.parking_get_data));
        progressBar.showProgressBar();

        getDeviceInfo();
        if (device_id != null && device_os_name != null
                && device_os_ver != null && other != null
                && device_type != 0 && device_vendor != null) {
            String service_code = "PRK";
            JsonObject accKitUserJson = new JsonObject();
            JsonObject accKitDeviceJson = new JsonObject();
            accKitDeviceJson.addProperty(Constants.DEVICE_ID, device_id);
            accKitDeviceJson.addProperty(Constants.DEVICE_OS_NAME, device_os_name);
            accKitDeviceJson.addProperty(Constants.DEVICE_OS_VER, device_os_ver);
            accKitDeviceJson.addProperty(Constants.DEVICE_OTHER, other);
            accKitDeviceJson.addProperty(Constants.DEVICE_TYPE, device_type);
            accKitDeviceJson.addProperty(Constants.DEVICE_VENDOR, device_vendor);

            accKitUserJson.addProperty("authorization_code", authorization_code);
            accKitUserJson.addProperty("service_code", service_code);
            accKitUserJson.add("devInfo", accKitDeviceJson);
            Log.e("authorization:", authorization_code);
            Log.e("service code:", service_code);
            Log.e("device info:", accKitDeviceJson.toString());

            Ion.with(MainActivity.this)
                    .load(Constants.SERVER_AUTHEN + Constants.AUTHEN_ACCOUNT_KIT)
                    .setJsonObjectBody(accKitUserJson)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                                 if (e != null){
                                     Log.e("Exception Acc kit: ", e.toString());
                                 } else {
                                     Log.e("Result acc kit: ", result.toString());
                                     Error errorModel = JsonParse.checkError(result.toString());
                                     if (errorModel != null){
                                         Log.e("ma loi: ", String.valueOf(errorModel.getCode()));
                                     } else {
                                         Log.e("Ma result acc kit:", result.toString());
                                         String authenticationid = result.get("authenticationid").getAsString();
                                         session = result.get("session").getAsString();
                                         long login_time = result.get("login_time").getAsLong();
                                         long expired_time = result.get("expired_time").getAsLong();
                                         int dev_info_status = result.get("dev_info_status").getAsInt();
                                         check_acc_kit = true;
                                         //Assign variable to AppPreferenc variable
                                         SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AUTH_ID, authenticationid);
                                         SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_SESSION, session);
                                         SharedPreferencesUtils.getInstance().putLongValue(Constants.USER_LOGIN_TIME, login_time);
                                         SharedPreferencesUtils.getInstance().putLongValue(Constants.USER_EXPIRED_TIME, expired_time);
                                         SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_DEV_INFO_STATUS, authenticationid);
                                         String results = "Auth_ID: " + authenticationid + ", Session: " + session + ", login time: " + login_time + ", Ex Time: " + expired_time + ", Device Info status: " + dev_info_status;
                                         Log.e("Test acckit result", results);
                                         String login_times = convertLong2Time(login_time);
                                         String expired = convertLong2Time(expired_time);
                                         String time_session = "Login time: " + login_times + ", Expired time: " + expired;
                                         Log.e("Time session: ", time_session);
                                         getJsonFromAuthen(session);
                                     }
                                 }
                        }
                    });
        }
    }

    public void getJsonFromAuthen(String session) {
        String URL_authen = Constants.SERVER_PARKING + Constants.GET_USER;
        Log.e("session mobile:", URL_authen);
        Ion.with(MainActivity.this)
                .load(URL_authen)
                .setHeader("session", session)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.e("Exception get user:", e.toString());
                        } else {
                            Log.e("Result user:", result.toString());
                            Error errorModel = JsonParse.checkError(result.toString());
                            if (errorModel != null) {
                                Log.e("loi boc data:", String.valueOf(errorModel.getCode()));
                            } else {
                                Gson gson = new Gson();
                                UserModel userModel = gson.fromJson(result.toString(), UserModel.class);
                                String fullname = userModel.getFullname();
                                int gender = userModel.getGender();
                                String birthday = userModel.getBirthday();
                                String phone = userModel.getPhone();
                                String address = userModel.getAddress();
                                String avatar = userModel.getAvatar();
                                String email = userModel.getEmail();
                                String qr_code = userModel.getQr_code();
                                String bar_code = userModel.getBar_code();

                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_FULL_NAME, fullname);
                                SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_GENDER, gender);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_BIRTH_DAY, birthday);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_PHONE, phone);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_ADDRESS, address);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AVATAR, avatar);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_EMAIL, email);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_QR, qr_code);
                                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_BAR, bar_code);
                                Log.e("User:", result.toString());
                            }
                        }
                    }
                });
    }

    private void delayStartActivity(int milisecond){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.closeProgressBar();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }, milisecond);
    }

    @Override
    protected void onDestroy() {
        try {
            tokenTracker.stopTracking();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showConfirmExitApp();
    }

    protected void showConfirmExitApp() {
        if (isWaitingForExit) {
//            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//            homeIntent.addCategory(Intent.CATEGORY_HOME);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(homeIntent);
            System.exit(0);
        } else {
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    isWaitingForExit = true;
                    showToast(getString(R.string.text_back_press_to_exit));

                }

                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    isWaitingForExit = false;
                }
            }.execute();
        }
    }

    private String showToast(String mes) {
        Toast.makeText(MainActivity.this, mes, Toast.LENGTH_SHORT).show();
        return mes;
    }

    public String convertLong2Time(long time) {
        Date date = new Timestamp(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        String formatTime = dateFormat.format(date);
        return formatTime;
    }
}
