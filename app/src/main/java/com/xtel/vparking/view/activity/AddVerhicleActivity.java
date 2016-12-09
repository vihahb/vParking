package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.VerhicleBrandName;
import com.xtel.vparking.presenter.AddVerhiclePresenter;
import com.xtel.vparking.view.activity.inf.AddVerhicleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivhp on 12/9/2016.
 */

public class AddVerhicleActivity extends BasicActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AddVerhicleView {

    private EditText edt_verhicle_name, edt_verhicle_plate, edt_verhicle_descriptions;
    private Spinner sp_verhicle_brandname;
    private CheckBox chk_verhicle_default;
    private Button btn_verhicle_add;
    AddVerhiclePresenter verhiclePresenter;

    private ArrayList<String> brandNames_arr;
    private ArrayAdapter<String> adapter_spinner_brand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_verhicle_activity);
        verhiclePresenter = new AddVerhiclePresenter(this);
        verhiclePresenter.getVerhicleBrandname();
        initToolbar();
        initView();
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_verhicle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        edt_verhicle_name = (EditText) findViewById(R.id.edt_verhicle_name);
        edt_verhicle_plate = (EditText) findViewById(R.id.edt_verhicle_plate);
        edt_verhicle_descriptions = (EditText) findViewById(R.id.edt_verhicle_des);
        sp_verhicle_brandname = (Spinner) findViewById(R.id.spinner_brandname);
        chk_verhicle_default = (CheckBox) findViewById(R.id.chk_verhicle_default);
        btn_verhicle_add = (Button) findViewById(R.id.btn_verhicle_add);
        brandNames_arr = new ArrayList<>();
        verhiclePresenter.getVerhicleBrandname();
    }

    private void initSpinnerBrandname(Context context) {
        adapter_spinner_brand = new ArrayAdapter<>(context, R.layout.simple_spinner_item, brandNames_arr);
        adapter_spinner_brand.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp_verhicle_brandname.setAdapter(adapter_spinner_brand);
        sp_verhicle_brandname.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_verhicle_add) {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int id_spinner = parent.getId();
        String brand_name;
        if (id_spinner == R.id.spinner_brandname) {
            brand_name = adapter_spinner_brand.getItem(position).toString();
            showShortToast(brand_name);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAddVerhicleSuccess() {

    }

    @Override
    public void onAddVerhicleError() {

    }

    @Override
    public void onGetBrandnameData(ArrayList<VerhicleBrandName> brandNames) {
        brandNames_arr = getNameFromBrand(brandNames);
        Log.v("brand 1", brandNames_arr.get(0).toString());
        initSpinnerBrandname(this);
    }

    private ArrayList<String> getNameFromBrand(ArrayList<VerhicleBrandName> brandNames) {
        ArrayList<String> name_brand = new ArrayList<>();
        if (brandNames != null) {
            int total = brandNames.size();
            if (total > 0) {
                Log.v("Total brand", "" + total);
                for (int i = 0; i < brandNames.size(); i++) {
                    name_brand.add(i, brandNames.get(i).getName().toString());
                    Log.v("arr name" + i, name_brand.toString());
                }
            }
        }
        return name_brand;
    }

    @Override
    public Activity getActivity() {
        return null;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
