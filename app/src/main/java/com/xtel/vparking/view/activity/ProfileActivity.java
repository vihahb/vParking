package com.xtel.vparking.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.dialog.DialogProgressBar;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.utils.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText edt_fname, edt_email, edt_ngaysinh, edt_phone;
    private Spinner spinner_gender;
    private boolean check_acc_kit;

    public static String[] gender_spinner = {"Nam", "Nữ", "Khác"};

    private Button btnUpdate, btn_clear, btn_clear_email;
    ImageView img_avatar, img_change_avatar, img_update_phone;
    DialogProgressBar progressBar;

    int year_fill, month_fill, dayOfMonthfill;

    //Uer Infomation
    String avatar;
    String full_name;
    String phone;
    int gender;
    int gender_update;
    int respond_type;
    String email;
    String birthday;
    String session_id;
    DatePickerDialog pickerDialog;
    ArrayAdapter<String> arrayAdapter;
    public static int ACC_REQUEST_CODE = 100;
    Calendar calendar;
    Date date;

    //update info
    String full_name_update;
    String email_update;
    String birthday_update;
    String phone_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        initWidget();
        initData();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initWidget() {
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
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, gender_spinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(arrayAdapter);
        spinner_gender.setOnItemSelectedListener(this);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton();
            }
        });

        img_change_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAvatar(v);
            }
        });

        edt_ngaysinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBirthday();
            }
        });

        img_update_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdatePhone(v);
            }
        });
        onFocusChangeEditText();
    }

    private void onFocusChangeEditText() {
        edt_fname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
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
                if (hasFocus){
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
                }
            });
        }
        if (type == 2) {
            btn_clear_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_email.setText("");
                }
            });
        }

    }

    private View.OnClickListener onClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_fname.setText("");
                Toast.makeText(ProfileActivity.this, "Cleaned!", Toast.LENGTH_SHORT).show();
            }
        };
    }


    private void initData() {
        session_id = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        avatar = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_AVATAR);
        full_name = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_FULL_NAME);
        phone = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_PHONE);
        gender = SharedPreferencesUtils.getInstance().getIntValue(Constants.USER_GENDER);
        email = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_EMAIL);
        birthday = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_BIRTH_DAY);
        check_acc_kit = SharedPreferencesUtils.getInstance().getBooleanValue(Constants.USER_ACC_KIT);
        date = new Date();
        calendar = Calendar.getInstance();
        if (avatar != null)
            Picasso.with(ProfileActivity.this)
                    .load(avatar)
                    .placeholder(R.mipmap.ic_user)
                    .error(R.mipmap.ic_user)
                    .into(img_avatar);

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


    private void updateBirthday() {
        Toast.makeText(this, "Click ngay sinh", Toast.LENGTH_SHORT).show();
        //Get curent Time
        pickerDialog = new DatePickerDialog(ProfileActivity.this, R.style.TimePicker,
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

    private void updateAvatar(View view) {
        Task.TakeBigPicture(ProfileActivity.this, getSupportFragmentManager(), true, new RequestWithStringListener() {
            @Override
            public void onSuccess(String url) {
                avatar = url;
                SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AVATAR, avatar);
                Picasso.with(ProfileActivity.this)
                        .load(avatar)
                        .error(R.mipmap.ic_user)
                        .into(img_avatar);
                updateUser(full_name, email, gender, birthday, avatar, phone, session_id);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void pushingData(String avatar_p, String fullname_p, int gender_p, String email_p, String birthday_p, String phone_p) {
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_AVATAR, avatar_p);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_FULL_NAME, fullname_p);
        SharedPreferencesUtils.getInstance().putIntValue(Constants.USER_GENDER, gender_p);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_EMAIL, email_p);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_BIRTH_DAY, birthday_p);
        SharedPreferencesUtils.getInstance().putStringValue(Constants.USER_PHONE, phone_p);
    }

    private boolean valid() {
        if (TextUtils.isEmpty(edt_fname.getText().toString())) {
            Toast.makeText(this, getString(R.string.update_message_failed_name), Toast.LENGTH_SHORT).show();
            progressBar.closeProgressBar();
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

    private void onClickButton() {
        progressBar = new DialogProgressBar(ProfileActivity.this, false, false, null, getString(R.string.update_message));
        progressBar.showProgressBar();

        if (valid()) {
            String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
            Log.e("Email field:", String.valueOf(email_update));
            updateUser(full_name_update, email_update, gender_update, birthday_update, avatar, phone_update, session);
            delayHandle(1000);
            pushingData(avatar, full_name_update, gender_update, email_update, birthday_update, phone_update);
        } else {
            Log.e("Error: ", "Co loi valid");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void updateUser(String fname, String email, int gender, String birthday, String ava, String number_phone, String session) {
        String url = Constants.SERVER_PARKING + Constants.UPDATE_USER;
        JsonObject userUpdate = new JsonObject();
        userUpdate.addProperty("fullname", fname);
        userUpdate.addProperty("gender", gender);
        userUpdate.addProperty("birthday", birthday);
        userUpdate.addProperty("email", email);
        userUpdate.addProperty("phone", number_phone);
        userUpdate.addProperty("avatar", ava);
        Log.e("Update object: ", String.valueOf(userUpdate));
        Log.v("url update:", url);
        Log.v("Session Profile:", session);
        Ion.with(ProfileActivity.this)
                .load("POST", url)
                .setHeader("session", session)
                .setJsonObjectBody(userUpdate)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e("Exception update user", e.toString());
                        } else {
                            Log.e("Test result update: ", "null k:  " + result.toString());
                            Error error = JsonParse.checkError(result.toString());
                            if (error != null) {
                                Log.e("Ma loi update: ", String.valueOf(error.getCode()));
                            } else {
                                Log.e("Thanh cong: ", result.toString());
                                Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void delayHandle(int milisecond) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.closeProgressBar();
            }
        }, milisecond);
    }

    public void onUpdatePhone(View view) {
        Intent intent = new Intent(ProfileActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        configurationBuilder.setDefaultCountryCode("VN");
        configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, ACC_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACC_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage = "";
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Verify Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            Log.e("phone: ", String.valueOf(account.getPhoneNumber()));
                            edt_phone.setText(String.valueOf(account.getPhoneNumber()));
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.e("Eror acc: ", accountKitError.toString());
                        }
                    });
                }
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
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
            Log.v("position: ", String.valueOf(position));
            gender_update = values;
            Log.v("gender update: ", String.valueOf(gender_update));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
