package com.xtel.vparking.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.accountkit.ui.AccountKitActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.UserModel;
import com.xtel.vparking.presenter.ProfilePresenter;
import com.xtel.vparking.utils.PermissionHelper;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.inf.ProfileView;
import com.xtel.vparking.view.widget.BitmapTransform;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by vivhp on 12/8/2016.
 */

public class ProfileActivitys extends BasicActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ProfileView {
    //Spinner Properties
    public static String[] gender_spinner = {"Nam", "Nữ", "Khác"};
    private final int CAMERA_REQUEST_CODE = 1002;
    ImageView img_avatar, img_change_avatar, img_update_phone;
    ArrayAdapter<String> arrayAdapter;
    ProfilePresenter profilePresenter;
    int year_fill, month_fill, dayOfMonthfill;
    Calendar calendar;
    Date date;
    DatePickerDialog pickerDialog;
    //Uer Infomation
    String avatar;
    String full_name;
    String phone;
    int gender;
    int gender_update;
    int respond_type;
    String email;
    String birthday;
    String qr_code;
    String bar_code;
    //update info
    String full_name_update;
    String email_update;
    String birthday_update;
    String phone_update;
    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private EditText edt_fname, edt_email, edt_ngaysinh, edt_phone;
    private Spinner spinner_gender;
    private Button btnUpdate, btn_clear, btn_clear_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilePresenter = new ProfilePresenter(this);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        edt_fname = (EditText) findViewById(R.id.edt_fullname);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_ngaysinh = (EditText) findViewById(R.id.edt_birth_date);
        edt_phone = (EditText) findViewById(R.id.edt_phone);

        img_avatar = (ImageView) findViewById(R.id.img_profile_avatar);
        img_change_avatar = (ImageView) findViewById(R.id.img_profile_change_avatar);
        img_update_phone = (ImageView) findViewById(R.id.img_update_phone);
        btnUpdate = (Button) findViewById(R.id.btn_profile_update);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear_email = (Button) findViewById(R.id.btn_clear_email);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);
        initSpinner();
        initOnclick();
        onFocusChangeEditText();
        initViewData(getApplicationContext());
    }

    private void initSpinner() {
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, gender_spinner);
        arrayAdapter.setDropDownViewResource(R.layout.simple_dropdown_item);
        spinner_gender.setAdapter(arrayAdapter);
        spinner_gender.setOnItemSelectedListener(this);
    }

    private void initOnclick() {
        btnUpdate.setOnClickListener(this);
        img_change_avatar.setOnClickListener(this);
        edt_ngaysinh.setOnClickListener(this);
        img_update_phone.setOnClickListener(this);
        onFocusChangeEditText();
    }

    private void initViewData(Context context) {
        UserModel userModel = profilePresenter.initData();

        avatar = userModel.getAvatar();
        full_name = userModel.getFullname();
        gender = userModel.getGender();
        email = userModel.getEmail();
        birthday = userModel.getBirthday();
        phone = userModel.getPhone();
        qr_code = userModel.getQr_code();
        bar_code = userModel.getBar_code();

        calendar = Calendar.getInstance();
        date = new Date();
        if (avatar != null) {
            Picasso.with(context)
                    .load(avatar)
                    .placeholder(R.mipmap.icon_account)
                    .error(R.mipmap.icon_account)
                    .into(img_avatar);
        } else {
            img_avatar.setImageResource(R.mipmap.icon_account_1);
        }

        //Gender spinner
        if (gender == 1) {
            spinner_gender.setSelection(0);
        } else if (gender == 2) {
            spinner_gender.setSelection(1);
        } else {
            spinner_gender.setSelection(2);
        }

        //Full name
        if (full_name != null && full_name != "") {
            edt_fname.setText(full_name);
        } else {
            full_name = "Chưa có tên";
            edt_fname.setHint(full_name);
        }

        //Email
        if (email != null && email != "") {
            edt_email.setText(email);
        } else {
            email = "Chưa có email";
            edt_email.setHint(email);
        }

        //phone
        if (phone != null && phone != "") {
            edt_phone.setText(phone);
        } else {
            phone = "Chưa có số điện thoại";
            edt_phone.setHint(phone);
        }

        //birthady
        if (birthday != null && birthday != "") {
            edt_ngaysinh.setText(birthday);
            DateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");

            try {
                date = simpleDateFormat.parse(birthday);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            year_fill = calendar.get(Calendar.YEAR);
            month_fill = calendar.get(Calendar.MONTH);
            dayOfMonthfill = calendar.get(Calendar.DAY_OF_MONTH);
            Log.e("Date time:", year_fill + "/" + month_fill + "/" + dayOfMonthfill);
            Log.e("Year:", String.valueOf(year_fill));
            Log.e("month:", String.valueOf(month_fill));
            Log.e("day:", String.valueOf(dayOfMonthfill));
        } else {
            birthday = "Chưa có ngày sinh";
            edt_ngaysinh.setHint(birthday);
            calendar.getTime();
            year_fill = calendar.get(Calendar.YEAR);
            month_fill = calendar.get(Calendar.MONTH);
            dayOfMonthfill = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    private void onFocusChangeEditText() {
        edt_fname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    respond_type = 1;
                    btn_clear.setVisibility(View.VISIBLE);
                    cleanEditText(respond_type);
                } else {
                    btn_clear.setVisibility(View.GONE);
                }

            }
        });

        edt_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    respond_type = 2;
                    btn_clear_email.setVisibility(View.VISIBLE);
                    cleanEditText(respond_type);
                } else {
                    btn_clear_email.setVisibility(View.GONE);
                }
            }
        });
    }

    private void cleanEditText(int type) {
        if (type == 1) {
            btn_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_fname.setText("");
                    showShortToast(getActivity().getString(R.string.clear_name));
                }
            });
        }
        if (type == 2) {
            btn_clear_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_email.setText("");
                    showShortToast(getActivity().getString(R.string.clear_email));
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_profile_update) {
            updateUser();
        } else if (id == R.id.img_profile_change_avatar) {
            updateAvatar(ProfileActivitys.this);
        } else if (id == R.id.edt_birth_date) {
            updateBirthday(this);
        } else if (id == R.id.img_update_phone) {
            updateMyPhone(ProfileActivitys.this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_gender) {
            String s = arrayAdapter.getItem(position).toString();
            int values;
            if (s.equals("Nam")) {
                values = 1;
            } else if (s.equals("Nữ")) {
                values = 2;
            } else {
                values = 3;
            }
            gender_update = values;
        }
    }

    private boolean valid() {
        if (TextUtils.isEmpty(edt_fname.getText().toString())) {
            showShortToast(getActivity().getString(R.string.update_message_failed_name));
            return false;
        } else {
            full_name_update = edt_fname.getText().toString();
        }
        if (TextUtils.isEmpty(edt_email.getText().toString())) {
            email_update = email;
        } else {
            email_update = edt_email.getText().toString();
        }

        if (TextUtils.isEmpty(edt_ngaysinh.getText().toString())) {
            birthday_update = birthday;
        } else {
            birthday_update = edt_ngaysinh.getText().toString();
        }
        if (TextUtils.isEmpty(edt_phone.getText().toString())) {
            phone_update = phone;
        } else {
            phone_update = edt_phone.getText().toString();
        }
        return true;
    }

    private void updateMyPhone(Context context) {
        checkNetwork(context, 3);
    }

    private void updateAvatar(final Context context) {
        checkNetwork(context, 2);
    }

    private void updateUser() {
        if (valid()) {
            checkNetwork(ProfileActivitys.this, 1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onUpdateSuccess() {

    }

    @Override
    public void onUpdateError() {

    }

    @Override
    public void onGetDataSuccess(UserModel userModel) {

    }

    private void updateBirthday(Context context) {
        Toast.makeText(this, "Click ngay sinh", Toast.LENGTH_SHORT).show();
        //Get curent Time
        pickerDialog = new DatePickerDialog(context, R.style.TimePicker,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dateSet = year + "/" + (month + 1) + "/" + dayOfMonth;
                        birthday = dateSet;
                        edt_ngaysinh.setText(birthday);
                        year_fill = year;
                        month_fill = month;
                        dayOfMonthfill = dayOfMonth;
                    }
                }, year_fill, month_fill, dayOfMonthfill);
        pickerDialog.show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showShortToast(String message) {
        super.showShortToast(message);
    }

    @Override
    public void showLongToast(String message) {
        super.showLongToast(message);
    }

    @Override
    public void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message) {
        super.showProgressBar(isTouchOutside, isCancel, title, message);
    }

    @Override
    public void closeProgressBar() {
        super.closeProgressBar();
    }

    @Override
    public void startActivityToLogin(Class clazz) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPostPictureSuccess(String url) {
        Picasso.with(this)
                .load(url)
                .placeholder(R.mipmap.icon_account)
                .error(R.mipmap.icon_account)
                .into(img_avatar);
        profilePresenter.updateAvatar(url);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AVATAR, url);
    }

    @Override
    public void onPostPictureError(String error) {
        showShortToast(error);
    }


    @Override
    public void updatePhone(String phone) {
        edt_phone.setText(phone);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePresenter.initResultAccountKit(requestCode, resultCode, data);
    }

    private void checkNetwork(final Context context, int type) {
        if (!NetWorkInfo.isOnline(context)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.TimePicker);
            dialog.setTitle("Kết nối không thành công");
            dialog.setMessage("Rất tiếc, không thể kết nối internet. Vui lòng kiểm tra kết nối Internet.");
            dialog.setPositiveButton("Cài đặt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        } else {
            if (type == 1) {
                profilePresenter.updateUser(full_name_update, email_update, birthday_update, gender_update, phone_update);
            } else if (type == 2) {
                if (PermissionHelper.checkListPermission(permission, this, CAMERA_REQUEST_CODE)) {
                    initCamera();
                }
            } else if (type == 3) {
                profilePresenter.onUpdatePhone(getApplicationContext(), AccountKitActivity.class);
            }
        }
    }

    private void initCamera() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(ProfileActivitys.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
                        showProgressBar(false, false, null, getActivity().getString(R.string.update_message));
                        Log.e("tb_uri", "uri: " + uri);
                        Log.e("tb_path", "uri.geta: " + uri.getPath());

                        Picasso.with(ProfileActivitys.this)
                                .load(uri)
                                .placeholder(R.mipmap.ic_parking_background)
                                .error(R.mipmap.ic_parking_background)
                                .transform(new BitmapTransform(1200, 1200))
                                .fit()
                                .centerCrop()
                                .into(img_avatar, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Bitmap bitmap = ((BitmapDrawable) img_avatar.getDrawable()).getBitmap();
                                        profilePresenter.postImage(bitmap);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                })
                .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            Log.e("size permission:", String.valueOf(grantResults.length));

            boolean chk_camera = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    chk_camera = false;
                    break;
                }
            }

            if (chk_camera) {
                showShortToast(getActivity().getString(R.string.permission_checked));
                initCamera();
            } else
                showShortToast(getActivity().getString(R.string.permission_not_check));
        }
    }
}
