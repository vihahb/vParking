package com.xtel.vparking.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.JsonObject;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Profile;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.model.entity.RESP_User;
import com.xtel.vparking.model.entity.UserModel;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.utils.Task;
import com.xtel.vparking.view.activity.LoginActivity;
import com.xtel.vparking.view.activity.inf.ProfileView;

/**
 * Created by vivhp on 12/8/2016.
 */

public class ProfilePresenter {

    public static int ACC_REQUEST_CODE = 100;
    public ProfileView view;
    private String phone_result;

    public ProfilePresenter(ProfileView view) {
        this.view = view;
    }

//    public boolean initCameraStorePermission() {
//        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (PermissionHelper.checkListPermission(permission, view.getActivity(), CAMERA_REQUEST_CODE)) {
//            return true;
//        } else
//            return false;
//    }

    public UserModel initData() {
        return Profile.getInstance().gettingUserFromShared();
    }

    public void updateAvatar(final String avatar) {
        String url = Constants.SERVER_PARKING + Constants.UPDATE_USER;
        final String session = LoginModel.getInstance().getSession();
        JsonObject avaUpdate = new JsonObject();
        avaUpdate.addProperty("avatar", avatar);
        Log.e("Ava object: ", String.valueOf(avaUpdate));
        Log.v("url update:", url);
        Log.v("Session Profile:", session);

        Profile.getInstance().updateUser(url, avaUpdate.toString(), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                Log.e(this.getClass().getSimpleName(), "Update object: " + obj.toString());
                view.closeProgressBar();
                view.showShortToast(view.getActivity().getString(R.string.update_message_success));
                gettingDataUser(session);
            }

            @Override
            public void onError(Error error) {
                view.closeProgressBar();
                if (error.getCode() == 2) {
                    getNewSessionAvatar(avatar);
                } else
                    Log.e("Update code: ", String.valueOf(error.getCode()));
                Log.e("Update message: ", error.getMessage());
            }
        });
    }

    public void updateUser(final String name, final String email, final String birthday, final int gender, final String phone) {
        view.showProgressBar(false, false, null, view.getActivity().getString(R.string.update_message));
        String url = Constants.SERVER_PARKING + Constants.UPDATE_USER;
        final String session = LoginModel.getInstance().getSession();

        JsonObject userUpdate = new JsonObject();
        userUpdate.addProperty("fullname", name);
        userUpdate.addProperty("gender", gender);
        userUpdate.addProperty("birthday", birthday);
        userUpdate.addProperty("email", email);
        userUpdate.addProperty("phone", phone);

        Log.e("Update object: ", String.valueOf(userUpdate));
        Log.v("url update:", url);
        Log.v("Session Profile:", session);

        Profile.getInstance().updateUser(url, userUpdate.toString(), session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.closeProgressBar();
                Log.e("Update object: ", obj.toString());
                view.showShortToast(view.getActivity().getString(R.string.update_message_success));
                gettingDataUser(session);
            }

            @Override
            public void onError(Error error) {
                view.closeProgressBar();
                if (error.getCode() == 2) {
                    getNewSessionUser(name, email, birthday, gender, phone);
                } else
                    Log.e("Update code: ", String.valueOf(error.getCode()));
                Log.e("Update message: ", error.getMessage());
            }
        });
    }

    public void getNewSessionAvatar(final String avatar) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                view.showShortToast(view.getActivity().getString(R.string.get_session_success));
                updateAvatar(avatar);
            }

            @Override
            public void onError() {
                view.showShortToast(view.getActivity().getString(R.string.error_session_invalid));
                view.startActivityToLogin(LoginActivity.class);

            }
        });
    }

    public void getNewSessionUser(final String name, final String email, final String birthday, final int gender, final String phone) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                view.showShortToast(view.getActivity().getString(R.string.get_session_success));
                updateUser(name, email, birthday, gender, phone);
            }

            @Override
            public void onError() {
                view.showShortToast(view.getActivity().getString(R.string.error_session_invalid));
                view.startActivityToLogin(LoginActivity.class);
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

    public void onUpdatePhone(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        configurationBuilder.setDefaultCountryCode("VN");
        configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        view.startActivityForResult(intent, ACC_REQUEST_CODE);
    }

    public void postImage(Bitmap bitmap) {
        new Task.ConvertImage(view.getActivity(), true, new RequestWithStringListener() {
            @Override
            public void onSuccess(String url) {
                view.onPostPictureSuccess(url);
            }

            @Override
            public void onError() {
                view.onPostPictureError("Xảy ra lỗi. Vui lòng thử lại");
            }
        }).execute(bitmap);
    }

    public void initResultAccountKit(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACC_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage = "";
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Verify Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            Log.e("phone: ", String.valueOf(account.getPhoneNumber()));
                            phone_result = String.valueOf(account.getPhoneNumber());
                            SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_PHONE, phone_result);
                            view.updatePhone(phone_result);
                            String result = "Update success: " + account.getPhoneNumber();
                            view.showShortToast(result);
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.e("Eror acc: ", accountKitError.toString());
                        }
                    });
                }
            }
            // Surface the result to your user in an appropriate way.
        }
    }

//    public void requestPermission(Context context, int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case CAMERA_REQUEST_CODE: {
//                Log.e("size permission:", String.valueOf(grantResults.length));
//
//                boolean chk_camera = true;
//                for (int grantResult : grantResults) {
//                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                        chk_camera = false;
//                        break;
//                    }
//                }
//
//                if (chk_camera) {
//                    view.showShortToast("Permission Granted!");
//                } else
//                    view.showShortToast(view.getActivity().getString(R.string.permission_not_check));
//            }
//
//            default:
//                break;
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
}
