package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Find;
import com.xtel.vparking.presenter.FindAdvancedPresenter;
import com.xtel.vparking.view.activity.inf.FindAdvancedView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class FindAdvancedActivity extends BasicActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, FindAdvancedView {
    private EditText edt_price, edt_begin_time, edt_end_time;
    private CheckBox chk_oto, chk_xemay, chk_xedap;
    private Button btn_find;
    private Spinner sp_price_type;
    //Spinner Data
    private ArrayAdapter<String> arrayAdapter;
    private String[] price_type = {"Tất cả", "Giờ", "Lượt", "Qua đêm"};
    int value_price_type;
    //Date Time Picker
    int hour, minutes;
    String value_time;

    int price_type_integer;
    FindAdvancedPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_advanced);
        presenter = new FindAdvancedPresenter(this);
        initToolbar();
        initWidget();
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
        edt_price = (EditText) findViewById(R.id.edt_price);
        edt_begin_time = (EditText) findViewById(R.id.edt_begin_time);
        edt_end_time = (EditText) findViewById(R.id.edt_expired_time);

        sp_price_type = (Spinner) findViewById(R.id.spinner_price_type);

        chk_oto = (CheckBox) findViewById(R.id.chk_find_advanced_oto);
        chk_xemay = (CheckBox) findViewById(R.id.chk_find_advanced_xemay);
        chk_xedap = (CheckBox) findViewById(R.id.chk_find_advanced_xedap);

        btn_find = (Button) findViewById(R.id.btn_find);
        initOnClick();
        initSpinner();
        initTime();
    }

    private void initOnClick(){
        btn_find.setOnClickListener(this);
        edt_begin_time.setOnClickListener(this);
        edt_end_time.setOnClickListener(this);
    }

    private void initTime(){
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        String time_now = getHour(hour) + ":" + getMinute(minutes);
        edt_begin_time.setText(time_now);
        edt_end_time.setText(time_now);
    }

    private void initSpinner(){
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, price_type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp_price_type.setAdapter(arrayAdapter);
        sp_price_type.setOnItemSelectedListener(this);
    }

    private int initCheckBox(View view){
        int type;

        if (chk_xemay.isChecked() && chk_oto.isChecked() && chk_xedap.isChecked()){
            type = 6;
            String mes = getActivity().getString(R.string.parking_all);
            showSnackbar(view, mes);
        } else if (chk_xemay.isChecked() && chk_oto.isChecked()){
            type = 5;
            String mes = getActivity().getString(R.string.parking_xeoto_xemay);
            showSnackbar(view, mes);
        } else  if (chk_xemay.isChecked() && chk_xedap.isChecked()){
            type = 4;
            String mes = getActivity().getString(R.string.parking_xemay_xedap);
            showSnackbar(view, mes);
        } else if (chk_xedap.isChecked() && chk_oto.isChecked()){
            type = 6;
            String mes = getActivity().getString(R.string.parking_all);
            showSnackbar(view, mes);
        } else if (chk_xemay.isChecked()){
            type = 3;
            String mes = getActivity().getString(R.string.parking_xemay);
            showSnackbar(view, mes);
        } else if (chk_oto.isChecked()){
            type = 2;
            String mes = getActivity().getString(R.string.parking_xeoto);
            showSnackbar(view, mes);
        } else if (chk_xedap.isChecked()){
            type = 1;
            String mes = getActivity().getString(R.string.parking_xedap);
            showSnackbar(view, mes);
        } else {
            type = 6;
            String mes = getActivity().getString(R.string.parking_all);
            showSnackbar(view, mes);
        }
        return type;
    }

    private void PickTimeDialogBegin(){
        TimePickerDialog pickerDialog = new TimePickerDialog(FindAdvancedActivity.this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                value_time = getHour(hourOfDay) + " : " + getMinute(minute);
                edt_begin_time.setText(value_time);
            }
        },hour, minutes, true);
        pickerDialog.show();
    }

    private void PickTimeDialogExpired(){
        TimePickerDialog pickerDialog = new TimePickerDialog(FindAdvancedActivity.this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                value_time = getHour(hourOfDay) + " : " + getMinute(minute);
                edt_end_time.setText(value_time);
            }
        },hour, minutes, true);
        pickerDialog.show();
    }

    private void getDataActivity(View view){
        if (edt_price.getText().toString().isEmpty() || checkNumberInput(edt_price.getText().toString()) <= 0){
            String mes = getActivity().getString(R.string.loi_chontien);
            showSnackbar(view, mes);
        } else {
            int type_parking = initCheckBox(view);
            int type_price = price_type_integer;
            int price = Integer.parseInt(edt_price.getText().toString());
            String begin_time = edt_begin_time.getText().toString();
            String end_time = edt_end_time.getText().toString();

            onParkingResult(type_parking, price, type_price, begin_time, end_time);
        }
    }

    private void onParkingResult(int type, int price, int price_type, String begin_time, String end_time){
        presenter.getParkingRequest(type, price, price_type, begin_time, end_time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_find){
            getDataActivity(v);
        } else if (id == R.id.edt_begin_time){
            PickTimeDialogBegin();
        } else if (id == R.id.edt_expired_time){
            PickTimeDialogExpired();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinner_id = parent.getId();
        String value_item;
        if (spinner_id == R.id.spinner_price_type){
            value_item = arrayAdapter.getItem(position).toString();
            if (value_item.equals("Giờ")){
                price_type_integer = 1;
            } else if (value_item.equals("Lượt")){
                price_type_integer = 2;
            } else if (value_item.equals("Qua đêm")){
                price_type_integer = 3;
            } else {
                price_type_integer = 0;
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

    private int checkNumberInput(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
