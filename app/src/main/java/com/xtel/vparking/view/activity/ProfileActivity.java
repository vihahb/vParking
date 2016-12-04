package com.xtel.vparking.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;

public class ProfileActivity extends AppCompatActivity {
    private ImageView img_avatar, img_camera;
    private RadioButton radio_nam, radio_nu;
    private TextView txt_name;
    private EditText edt_fname, edt_lname, edt_ngaysinh, edt_phone, edt_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        initWidget();
        initData();
        gettingDataUpdate();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initWidget() {
        img_avatar = (ImageView) findViewById(R.id.img_profile_avatar);
        img_camera = (ImageView) findViewById(R.id.img_profile_camera);
        radio_nam = (RadioButton) findViewById(R.id.radio_nam);
        radio_nu = (RadioButton) findViewById(R.id.radio_nu);
        txt_name = (TextView) findViewById(R.id.txt_profile_name);
        edt_fname = (EditText) findViewById(R.id.edt_profile_fname);
        edt_lname = (EditText) findViewById(R.id.edt_profile_lname);
        edt_ngaysinh = (EditText) findViewById(R.id.edt_profile_birth);
        edt_phone = (EditText) findViewById(R.id.edt_profile_phone);
        edt_addr = (EditText) findViewById(R.id.edt_profile_address);
    }

    private void getData() {
        String session_id = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        Log.e("Session: ", session_id);
    }



    private void initData() {
        getData();
        String avatar = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_AVATAR);
        String first_name = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_FIRST_NAME);
        String last_name = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_LAST_NAME);
        String phone = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_PHONE);
        int sex = SharedPreferencesUtils.getInstance().getIntValue(Constants.USER_GENDER);
        String address = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_ADDRESS);
        String email = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_EMAIL);
        long birthday = SharedPreferencesUtils.getInstance().getLongValue(Constants.USER_BIRTH_DAY);
        String session_id = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        String full_name = first_name + " " + last_name;

        if (avatar != null)
            Picasso.with(ProfileActivity.this)
            .load(avatar)
            .placeholder(R.mipmap.ic_user)
            .error(R.mipmap.ic_user)
            .into(img_avatar);

//        if (sex > 0 && sex <= 3){
        if (sex == 1) {
            radio_nam.setChecked(true);
        } else if (sex == 2) {
            radio_nu.setChecked(true);
        } else {
            radio_nu.setChecked(false);
            radio_nam.setChecked(false);
        }
//        }

        txt_name.setText(full_name);
        edt_fname.setText(first_name);
        edt_lname.setText(last_name);
        edt_phone.setText(phone);
        edt_ngaysinh.setText(String.valueOf(birthday));
        edt_addr.setText(address);
    }

    public void gettingDataUpdate() {
        String fname = "Vi";
        String lname = "Hà";
        String phone = "01673378303";
        String address = "Hòa Bình";
        String ava = "https://scontent.xx.fbcdn.net/v/t1.0-1/c0.19.50.50/p50x50/13528706_849870591811691_1547349153482224738_n.jpg?oh=62ae45f806f985deda568808af610c87&oe=58CE1F8B";
        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
        int gender = 1;
        long birthday = 838495647;

        updateUser(fname, lname, gender, birthday, phone, address, ava, session);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void updateUser(String fname, String lname, int gender, long birthday, String phone, String address, String ava, String session) {
        JsonObject userUpdate = new JsonObject();
        userUpdate.addProperty("first_name", fname);
        userUpdate.addProperty("last_name", lname);
        userUpdate.addProperty("gender", gender);
        userUpdate.addProperty("birth_day", birthday);
        userUpdate.addProperty("phone", phone);
        userUpdate.addProperty("address", address);
        userUpdate.addProperty("avatar", ava);
        userUpdate.addProperty("session_id", session);
        Log.e("Update object: ", String.valueOf(userUpdate));
        Ion.with(ProfileActivity.this)
                .load("PUT", Constants.SERVER_AUTHEN + Constants.UPDATE_USER )
                .setHeader("session", session)
                .setJsonObjectBody(userUpdate)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.e("Exception update user", e.toString());
                        } else {
                            Log.e("Test result update: ", result.toString());
                            Error error = JsonParse.checkError(result.toString());
                            if (error != null) {
                                Log.e("Ma loi update: ", String.valueOf(error.getCode()));
                            } else {
                                Log.e("Thanh cong: ", result.toString());
                            }
                        }
                    }
                });
    }
}
