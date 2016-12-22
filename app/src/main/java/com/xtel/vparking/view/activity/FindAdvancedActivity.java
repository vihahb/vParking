package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Find;
import com.xtel.vparking.presenter.FindAdvancedPresenter;
import com.xtel.vparking.view.activity.inf.FindAdvancedView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FindAdvancedActivity extends BasicActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, FindAdvancedView {
    private EditText edt_begin_time, edt_end_time;
    private CheckBox chk_oto, chk_xemay, chk_xedap;
    private Button btn_find, btn_clear;
    private Spinner sp_price_type, sp_price;
    private Menu menu;
    //Spinner Data
    private ArrayAdapter<String> arrayAdapter, priceAdapter;
    private String[] price_type = {"Tất cả", "Giờ", "Lượt", "Qua đêm"};
    private ArrayList<String> arrPrice;
    //Date Time Picker
    int hour_begin, minutes_begin;
    int hour_end, minutes_end;
    String value_time;
    int price_type_integer;
    int price;
    FindAdvancedPresenter presenter;
    Find findModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_advanced);
        presenter = new FindAdvancedPresenter(this);
        initToolbar();
        initWidget();
        validModel();
//        initSelectMoney();
    }

    //Set back narrow on Tool Bar
    @SuppressWarnings("ConstantConditions")
    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_find);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initWidget() {
        edt_begin_time = (EditText) findViewById(R.id.edt_begin_time);
        edt_end_time = (EditText) findViewById(R.id.edt_expired_time);

        sp_price = (Spinner) findViewById(R.id.spinner_price);
        sp_price_type = (Spinner) findViewById(R.id.spinner_price_type);

        chk_oto = (CheckBox) findViewById(R.id.chk_find_advanced_oto);
        chk_xemay = (CheckBox) findViewById(R.id.chk_find_advanced_xemay);
        chk_xedap = (CheckBox) findViewById(R.id.chk_find_advanced_xedap);

        btn_find = (Button) findViewById(R.id.btn_find);
//        btn_clear = (Button) findViewById(R.id.btn_clear_filter);
//        getData();
        initOnClick();
        initSpinner();
        initTimeBegin();
        initTimeEnd();
    }

    private void initOnClick() {
        btn_find.setOnClickListener(this);
        edt_begin_time.setOnClickListener(this);
        edt_end_time.setOnClickListener(this);
//        btn_clear.setOnClickListener(this);
    }

    private void initTimeBegin() {
        Calendar calendar = Calendar.getInstance();
        hour_begin = 00;
        minutes_begin = 01;
        String time_begin = getHour(hour_begin) + ":" + getMinute(minutes_begin);
        edt_begin_time.setText(time_begin);
    }

    private void initTimeEnd() {
        hour_end = 23;
        minutes_end = 59;
        String time_end = getHour(hour_end) + ":" + getMinute(minutes_end);
        edt_end_time.setText(time_end);
    }

    private void initPrice() {
        arrPrice = new ArrayList<>();
        arrPrice.add("Tất cả");
        for (int i = 1; i <= 95; i++) {
            if (i % 5 == 0) {
                arrPrice.add(String.valueOf(i));
                Log.v("int ", String.valueOf(i));
            }
        }
    }

    private void initSpinner() {
        initPrice();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_item_find, price_type);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_find_ad);
        sp_price_type.setAdapter(arrayAdapter);
        sp_price_type.setOnItemSelectedListener(this);

        priceAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_item_find, arrPrice);
        priceAdapter.setDropDownViewResource(R.layout.custom_spinner_find_ad);
        sp_price.setAdapter(priceAdapter);
        sp_price.setOnItemSelectedListener(this);
    }

    private int initCheckBox() {
        int type;

        if (chk_xemay.isChecked() && chk_oto.isChecked() && chk_xedap.isChecked()) {
            type = 6;
            String mes = getActivity().getString(R.string.parking_all);
            showShortToast(mes);
        } else if (chk_xemay.isChecked() && chk_oto.isChecked()) {
            type = 5;
            String mes = getActivity().getString(R.string.parking_xeoto_xemay);
            showShortToast(mes);
        } else if (chk_xemay.isChecked() && chk_xedap.isChecked()) {
            type = 4;
            String mes = getActivity().getString(R.string.parking_xemay_xedap);
            showShortToast(mes);
        } else if (chk_xedap.isChecked() && chk_oto.isChecked()) {
            type = 6;
            String mes = getActivity().getString(R.string.parking_all);
            showShortToast(mes);
        } else if (chk_xemay.isChecked()) {
            type = 3;
            String mes = getActivity().getString(R.string.parking_xemay);
            showShortToast(mes);
        } else if (chk_oto.isChecked()) {
            type = 2;
            String mes = getActivity().getString(R.string.parking_xeoto);
            showShortToast(mes);
        } else if (chk_xedap.isChecked()) {
            type = 1;
            String mes = getActivity().getString(R.string.parking_xedap);
            showShortToast(mes);
        } else {
            type = 6;
            String mes = getActivity().getString(R.string.parking_all);
            showShortToast(mes);
        }
        return type;
    }

    private void PickTimeDialogBegin() {
        TimePickerDialog pickerDialog = new TimePickerDialog(FindAdvancedActivity.this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                value_time = getHour(hourOfDay) + ":" + getMinute(minute);
                edt_begin_time.setText(value_time);
            }
        }, hour_begin, minutes_begin, true);
        pickerDialog.show();
    }

    private void PickTimeDialogExpired() {
        TimePickerDialog pickerDialog = new TimePickerDialog(FindAdvancedActivity.this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                value_time = getHour(hourOfDay) + ":" + getMinute(minute);
                edt_end_time.setText(value_time);
            }
        }, hour_end, minutes_end, true);
        pickerDialog.show();
    }

    private void getDataActivity() {
        int type_parking;
        int type_price;
        String begin_time;
        String end_time;

        type_parking = initCheckBox();
        type_price = price_type_integer;
        begin_time = edt_begin_time.getText().toString();
        end_time = edt_end_time.getText().toString();

        onParkingResult(type_parking, price, type_price, begin_time, end_time);
    }

    private void onParkingResult(int type, int price, int price_type, String begin_time, String end_time) {
        presenter.getParkingRequest(type, price, price_type, begin_time, end_time);
    }

    private void onResetParkingResult() {
        presenter.getParkingRequest(-1, -1, -1, "", "");
        String messages = "Huỷ bỏ lọc bãi đỗ";
        showShortToast(messages);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.filter_parking_clear) {
            onResetParkingResult();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_find) {
            getDataActivity();
        } else if (id == R.id.edt_begin_time) {
            PickTimeDialogBegin();
        } else if (id == R.id.edt_expired_time) {
            PickTimeDialogExpired();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinner_id = parent.getId();
        String value_item;
        String price_item;
        if (spinner_id == R.id.spinner_price_type) {
            value_item = arrayAdapter.getItem(position).toString();
            if (value_item.equals("Giờ")) {
                price_type_integer = 1;
            } else if (value_item.equals("Lượt")) {
                price_type_integer = 2;
            } else if (value_item.equals("Qua đêm")) {
                price_type_integer = 3;
            } else {
                price_type_integer = 0;
            }
        }
        if (spinner_id == R.id.spinner_price) {
            price_item = priceAdapter.getItem(position).toString();
            if (price_item.equals("Tất cả")) {
                price = -1;
            } else {
                price = Integer.parseInt(price_item);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String getHour(int hour) {
        if (hour < 10)
            return "0" + hour;
        else
            return String.valueOf(hour);
    }

    private String getMinute(int minute) {
        if (minute < 10)
            return "0" + minute;
        else
            return String.valueOf(minute);
    }

    @Override
    public void onFindSuccess() {

    }

    @Override
    public void onFindError() {

    }

    @Override
    public void putExtras(String key, Find find) {
        Intent intent = new Intent();
        intent.putExtra(key, find);
        setResult(Constants.FIND_ADVANDCED_RS, intent);
        finish();
    }

    @Override
    public void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
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
    public Activity getActivity() {
        return this;
    }

    private void setCheckBox(int type) {
        if (type == 1) {
            chk_xedap.setChecked(true);
        } else if (type == 2) {
            chk_oto.setChecked(true);
        } else if (type == 3) {
            chk_xemay.setChecked(true);
        } else if (type == 4) {
            chk_xemay.setChecked(true);
            chk_xedap.setChecked(true);
        } else if (type == 5) {
            chk_xemay.setChecked(true);
            chk_oto.setChecked(true);
        } else if (type == 6) {
            chk_xemay.setChecked(true);
            chk_oto.setChecked(true);
            chk_xedap.setChecked(true);
        } else {
            chk_oto.setChecked(false);
            chk_xemay.setChecked(false);
            chk_xedap.setChecked(false);
        }
    }

    private void setBegin(String begin_time) {
        String begintime = parseHour(begin_time) + ":" + parseMinutes(begin_time);
        edt_begin_time.setText(begintime);
        hour_begin = parseHour(begin_time);
        minutes_begin = parseMinutes(begin_time);
    }

    private void setEnd(String end_time) {
        String endtime = parseHour(end_time) + ":" + parseMinutes(end_time);
        edt_end_time.setText(endtime);
        hour_end = parseHour(end_time);
        minutes_end = parseMinutes(end_time);
    }

    private void setPriceType(int price_types) {
        if (price_types == 1) {
            sp_price_type.setSelection(1);
        } else if (price_types == 2) {
            sp_price_type.setSelection(2);
        } else if (price_types == 3) {
            sp_price_type.setSelection(3);
        } else {
            sp_price_type.setSelection(0);
        }
    }

    private int parseHour(String time) {
        int hout_parse = 0;
        Calendar calendar = Calendar.getInstance();
        DateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
        try {
            Date date = timeFormat.parse(time);
            calendar.setTime(date);
            hout_parse = calendar.get(Calendar.HOUR_OF_DAY);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hout_parse;
    }

    private int parseMinutes(String time) {
        int minutes_parse = 0;
        Calendar calendar = Calendar.getInstance();
        DateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
        try {
            Date date = timeFormat.parse(time);
            calendar.setTime(date);
            minutes_parse = calendar.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return minutes_parse;
    }

    private void validModel() {
        try {
            findModel = new Find();
            findModel = (Find) getIntent().getSerializableExtra(Constants.FIND_MODEL);
            Log.v("Begin time", findModel.getBegin_time());
        } catch (Exception e) {
            Log.e("Loi get find", e.getMessage());
        }

        if (findModel.getBegin_time().isEmpty()) {
            initTimeBegin();
        } else {
            String begin_time = findModel.getBegin_time();
            setBegin(begin_time);
        }

        if (findModel.getEnd_time().isEmpty()) {
            initTimeEnd();
        } else {
            String end_time = findModel.getEnd_time();
            setEnd(end_time);
        }

        if (findModel.getPrice_type() == -1) {
            initSpinner();
        } else {
            int price_type = findModel.getPrice_type();
            setPriceType(price_type);
        }


        if (findModel.getType() == -1) {
            setCheckBox(0);
        } else {
            int type = findModel.getType();
            setCheckBox(type);
        }

        if (findModel.getPrice() == -1) {
            sp_price.setSelection(0);
        } else {
            initPrice();
            int price = findModel.getPrice();
            String s_price = String.valueOf(price);
            for (int i = 0; i < arrPrice.size(); i++) {
                if (s_price == arrPrice.get(i).toString()) {
                    sp_price.setSelection(i);
                    Log.v("Spinner price pos", String.valueOf(i));
                }
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_advanced, menu);
        this.menu = menu;
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
