package com.xtel.vparking.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

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
import com.xtel.vparking.R;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.entity.AuthenNipModel;
import com.xtel.vparking.model.entity.DeviceObject;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_FLAG;
import com.xtel.vparking.model.entity.RESP_Login;
import com.xtel.vparking.model.entity.RESP_User;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.PermissionHelper;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.inf.LoginView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by vivhp on 12/8/2016.
 */

public class LoginPresenter {
    public static int ACC_REQUEST_CODE = 99;
    private final int MY_REQUEST_CODE = 1001;
    public LoginView view;
    CallbackManager callbackManager;
    AccessTokenTracker tokenTracker;
    AccessToken accessToken;
    String user_token;
    String authorization_code;

    /**
     * Device Data
     **/
    String device_id, device_os_name, device_os_ver, device_vendor;
    String other = "Not Yet";
    int device_type;

    boolean isWaitingForExit;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    public void initOnLoginFacebook(Activity activity) {
        //Login
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "email", "user_birthday"));
    }

    public void initOnLoginAccountKit(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE, AccountKitActivity.ResponseType.CODE);
        configurationBuilder.setDefaultCountryCode("VN");
        configurationBuilder.setTitleType(AccountKitActivity.TitleType.LOGIN);
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        view.startActivityForResult(intent, ACC_REQUEST_CODE);
    }

    public void createCallBackManager(final Context context) {
        AppEventsLogger.activateApp(context);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                checkAccessToken();
                Log.e("Access Token:", loginResult.getAccessToken().getToken());
                user_token = loginResult.getAccessToken().getToken();
                initAccountFacebook(context);
            }

            @Override
            public void onCancel() {
                view.showShortToast(view.getActivity().getString(R.string.signin_canceled));
            }

            @Override
            public void onError(FacebookException error) {
                view.showShortToast("Facebook Exception: " + error);
            }
        });
    }

    public void initAccKitPermission() {
        String[] PermissionList = {Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE};
        if (PermissionHelper.checkListPermission(PermissionList, view.getActivity(), MY_REQUEST_CODE)) {
            initOnLoginAccountKit(view.getActivity(), AccountKitActivity.class);
        }
    }

    public void initFacebookPermission() {
        String[] PermissionList = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (PermissionHelper.checkListPermission(PermissionList, view.getActivity(), MY_REQUEST_CODE)) {
            initOnLoginFacebook(view.getActivity());
        }
    }

    public void requestPermission(Context context, int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                Log.e("size permission:", String.valueOf(grantResults.length));

                boolean chk = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        chk = false;
                        break;
                    }
                }

                if (chk) {
                    getDeviceData(context);
                    view.showShortToast(view.getActivity().getString(R.string.permission_checked));
                } else
                    view.showShortToast(view.getActivity().getString(R.string.permission_not_check));
            }

            default:
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void requestCallbackManager(int requestCode, int resultCode, Intent data) {
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    public void requestAccountKitResult(int requestCode, int resultCode, Intent data, Context context) {
        if (requestCode == ACC_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toas_mes;
            if (loginResult.getError() != null) {
                toas_mes = loginResult.getError().getErrorType().getMessage();
                Log.e("Loi acc kit: ", loginResult.getError().toString());
            } else if (loginResult.wasCancelled()) {
                toas_mes = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toas_mes = "Success: " + loginResult.getAccessToken().getAccountId();
                    view.startActivityAndFinish(HomeActivity.class);
                } else {
                    toas_mes = "Success";
                    Log.e("Authorization Id: ", loginResult.getAuthorizationCode().toString());
                    authorization_code = loginResult.getAuthorizationCode().toString();
                    initAccountKit(context);
                }
            }
            view.showShortToast(toas_mes);
        }
    }

    public void initAccountFacebook(Context Activity) {
        view.showProgressBar(false, false, null, view.getActivity().getString(R.string.parking_get_data));
        DeviceObject device = getDeviceData(Activity);
        String url = Constants.SERVER_AUTHEN + Constants.AUTHEN_FB_LOGIN;

        if (validDevice()) {
            AuthenNipModel nipModel = new AuthenNipModel();
            nipModel.setAccess_token_key(user_token);
            nipModel.getService_code();
            nipModel.setDevInfo(device);

            Log.v("Nip Model:", JsonHelper.toJson(nipModel));
            Log.e("Test device Object: ", JsonHelper.toJson(device));
            LoginModel.getInstance().postFbData2Server(url, JsonHelper.toJson(nipModel), new ResponseHandle<RESP_Login>(RESP_Login.class) {
                @Override
                public void onSuccess(RESP_Login obj) {
                    String auth_id = obj.getAuthenticationid();
                    String session = obj.getSession();
                    long login_time = obj.getLogin_time();
                    long expired_time = obj.getExpired_time();
                    Log.v("Object authent: ", obj.toString());
                    checkTime(login_time, expired_time);
                    LoginModel.getInstance().savingData2Shared(auth_id, session, login_time, expired_time);
                    gettingFlagData(session);
                }

                @Override
                public void onError(Error error) {
                    view.closeProgressBar();
                    Log.e("Ma loi Fb login:", String.valueOf(error.getCode()));
                    Log.e("Message: ", error.getMessage());
                }
            });
        } else {
            Log.e("Loi Get Device", "");
        }
    }

    public void initAccountKit(Context context) {
        view.showProgressBar(false, false, null, view.getActivity().getString(R.string.parking_get_data));
        DeviceObject device = getDeviceData(context);
        String url = Constants.SERVER_AUTHEN + Constants.AUTHEN_ACCOUNT_KIT;

        if (validDevice()) {
            AuthenNipModel nipModel = new AuthenNipModel();
            nipModel.setAuthorization_code(authorization_code);
            nipModel.getService_code();
            nipModel.setDevInfo(device);

            Log.e("Nip Model:", JsonHelper.toJson(nipModel));

            LoginModel.getInstance().postAccountKitData2Server(url, JsonHelper.toJson(nipModel), new ResponseHandle<RESP_Login>(RESP_Login.class) {
                @Override
                public void onSuccess(RESP_Login obj) {
                    String auth_id = obj.getAuthenticationid();
                    String session = obj.getSession();
                    long login_time = obj.getLogin_time();
                    long expired_time = obj.getExpired_time();
                    Log.v("Object Account kit: ", obj.toString());
                    checkTime(login_time, expired_time);
                    LoginModel.getInstance().savingData2Shared(auth_id, session, login_time, expired_time);
                    gettingFlagData(session);
                }

                @Override
                public void onError(Error error) {
                    Log.e("Ma loi acc login:", String.valueOf(error.getCode()));
                    Log.e("Message: ", error.getMessage());
                }
            });

        } else {
            Log.e("Loi Get Device", "");
        }
    }

    public DeviceObject getDeviceData(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        //Getting device info
        device_id = telephonyManager.getDeviceId();
        device_os_name = android.os.Build.VERSION.CODENAME;
        device_os_ver = android.os.Build.VERSION.RELEASE;
        device_type = 1;
        device_vendor = android.os.Build.MANUFACTURER;
        Log.e("Device info: ", "Name: " + device_vendor + ", Android name: " + device_os_name + ", version: " + device_os_ver + ", id: " + device_id);
        DeviceObject deviceObject = new DeviceObject();
        deviceObject.setDeviceid(device_id);
        deviceObject.setOs_name(device_os_name);
        deviceObject.setOs_version(device_os_ver);
        deviceObject.setType(device_type);
        deviceObject.setVendor(device_vendor);
        return deviceObject;
    }

    private boolean validDevice() {
        if (device_id != null && device_os_name != null
                && device_os_ver != null && other != null
                && device_type != 0 && device_vendor != null) {
            return true;
        }
        return false;
    }

    public void gettingFlagData(final String sesion) {
        String url_flag = Constants.SERVER_PARKING + Constants.GET_FLAG;
        LoginModel.getInstance().gettingFlag(url_flag, sesion, new ResponseHandle<RESP_FLAG>(RESP_FLAG.class) {
            @Override
            public void onSuccess(RESP_FLAG obj) {
                int parking_flag = obj.getParking_flag();
                Log.v("Object flag: ", obj.toString());
                LoginModel.getInstance().postingFlag2Shared(parking_flag);
                gettingDataUser(sesion);
            }

            @Override
            public void onError(Error error) {
                view.closeProgressBar();
                Log.e("Ma loi Flag:", String.valueOf(error.getCode()));
                Log.e("Message Flag: ", error.getMessage());
            }
        });
    }

    public void gettingDataUser(String session) {
        String url_user = Constants.SERVER_PARKING + Constants.GET_USER;
        LoginModel.getInstance().gettingUser(url_user, session, new ResponseHandle<RESP_User>(RESP_User.class) {
            @Override
            public void onSuccess(RESP_User obj) {
                String fullname = obj.getFullname();
                int gender = obj.getGender();
                String birthday = obj.getBirthday();
                String email = obj.getEmail();
                String phone = obj.getPhone();
                String avatar = obj.getAvatar();
                String qr_code = obj.getQr_code();
                String bar_code = obj.getBar_code();
                Log.v("Object user: ", obj.toString());
                LoginModel.getInstance().postingUser2Shared(fullname, gender, birthday, email, phone, avatar, qr_code, bar_code);
                view.startActivityAndFinish(HomeActivity.class);
                view.closeProgressBar();
            }

            @Override
            public void onError(Error error) {
                view.closeProgressBar();
                Log.e("Ma loi user:", String.valueOf(error.getCode()));
                Log.e("Message user: ", error.getMessage());
            }
        });
    }

    public void checkAccessToken() {
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

    private String convertLong2Time(long time) {
        Date date = new Timestamp(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        String formatTime = dateFormat.format(date);
        return formatTime;
    }

    private void checkTime(long login_time, long expired_time) {
        String time = "login: " + convertLong2Time(login_time) + ", Expired: " + convertLong2Time(expired_time);
        Log.v("Time login 2 SV: ", time);
    }

    public void stopTracking() {
        try {
            tokenTracker.stopTracking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showConfirmExitApp() {
        if (isWaitingForExit) {
            System.exit(0);
        } else {
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    isWaitingForExit = true;
                    view.showShortToast(view.getActivity().getString(R.string.text_back_press_to_exit));
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
}
