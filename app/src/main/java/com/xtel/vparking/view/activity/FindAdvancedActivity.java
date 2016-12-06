package com.xtel.vparking.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Find;

public class FindAdvancedActivity extends AppCompatActivity {
    private EditText edt_place_number;
    private RadioButton chk_tatca, chk_oto, chk_xemay;
    private SeekBar seekBar_money;
    private TextView txt_money;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_advanced);

        initToolbar();
        initWidget();
        initSelectMoney();
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
        edt_place_number = (EditText) findViewById(R.id.edt_find_advanced_empty);

        chk_tatca = (RadioButton) findViewById(R.id.chk_find_advanced_tatca);
        chk_oto = (RadioButton) findViewById(R.id.chk_find_advanced_oto);
        chk_xemay = (RadioButton) findViewById(R.id.chk_find_advanced_xemay);

        seekBar_money = (SeekBar) findViewById(R.id.seek_bar_find_advanced_money);

        txt_money = (TextView) findViewById(R.id.txt_find_advanced_money);
    }

    private void initSelectMoney() {
        seekBar_money.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String money = (progress * 5) + "K";
                txt_money.setText(money);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void addPlaceSearch(View view) {
        int place = getPlaceNumber();

        if (place > -1) {
            place = place + 1;
            edt_place_number.setText(String.valueOf(place));
        }
    }

    public void removePlaceSearch(View view) {
        int place = getPlaceNumber();

        if (place > 0) {
            place = place - 1;
            edt_place_number.setText(String.valueOf(place));
        }
    }

    private int getPlaceNumber() {
        try {
            if (edt_place_number.getText().toString().isEmpty())
                return 0;
            return Integer.valueOf(edt_place_number.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void TimKiem(View view) {
        if (checkData(view)) {
            Find find = new Find();
            find.setType(getParkingType());
            find.setPlace_empty(Integer.parseInt(edt_place_number.getText().toString()));

            int money = (seekBar_money.getProgress() * 5) * 1000;
            find.setMoney(money);

            Intent intent = new Intent();
            intent.putExtra(Constants.FIND_MODEL, find);
            setResult(Constants.FIND_ADVANDCED_RS, intent);
            finish();
        }
    }

    private boolean checkData(View view) {
        if (edt_place_number.getText().toString().isEmpty() || checkNumberInput(edt_place_number.getText().toString()) <= 0) {
            Snackbar.make(view, getString(R.string.loi_chotrong), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (seekBar_money.getProgress() < 0) {
            Snackbar.make(view, getString(R.string.loi_chontien), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int checkNumberInput(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int getParkingType() {
        if (chk_tatca.isChecked())
            return 6;
        if (chk_oto.isChecked())
            return 2;
        if (chk_xemay.isChecked())
            return 3;
        return 6;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
