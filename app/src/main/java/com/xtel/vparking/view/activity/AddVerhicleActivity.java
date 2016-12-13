package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Brandname;
import com.xtel.vparking.model.entity.RESP_Verhicle;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.AddVerhiclePresenter;
import com.xtel.vparking.view.activity.inf.AddVerhicleView;
import com.xtel.vparking.view.adapter.CustomAddVerhicleAdapterSpinner;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/9/2016.
 */

public class AddVerhicleActivity extends BasicActivity implements AdapterView.OnItemSelectedListener, AddVerhicleView {

    private EditText edt_verhicle_name, edt_verhicle_plate, edt_verhicle_descriptions;
    private Spinner sp_verhicle_brandname;
    private CheckBox chk_verhicle_default;
    private Button btn_verhicle_add, btn_verhicle_update;
    private RadioGroup radioGroup;
    private RadioButton rdo_oto, rdo_xemay;
    AddVerhiclePresenter verhiclePresenter;
    String mess;
    int getId;
    String brand_code;
    Verhicle verhicle;

    private ArrayList<Brandname> brandNames_arr;
    private CustomAddVerhicleAdapterSpinner adapter_spinner_brand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verhicle);
        verhiclePresenter = new AddVerhiclePresenter(this);
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
        btn_verhicle_update = (Button) findViewById(R.id.btn_update_verhicle);
        radioGroup = (RadioGroup) findViewById(R.id.rdo_group);
        rdo_oto = (RadioButton) findViewById(R.id.rdo_oto);
        rdo_xemay = (RadioButton) findViewById(R.id.rdo_xemay);
        brandNames_arr = new ArrayList<Brandname>();
        verhiclePresenter.getVerhicleBrandname();
        getDataFromManager();
        OnClickButton();
    }

    private void initSpinnerBrandname() {
        adapter_spinner_brand = new CustomAddVerhicleAdapterSpinner(this, R.layout.custom_spinner_addverhicle, brandNames_arr);
        adapter_spinner_brand.setDropDownViewResource(R.layout.custom_spinner_addverhicle);
        sp_verhicle_brandname.setAdapter(adapter_spinner_brand);
        sp_verhicle_brandname.setOnItemSelectedListener(this);

        String brand_code = verhicle.getBrandname().getCode();
        for (int i = 0; i < brandNames_arr.size(); i++) {
            if (brand_code.equals(brandNames_arr.get(i).getCode())) {
                sp_verhicle_brandname.setSelection(i);
                Log.v("Spinner item pós", String.valueOf(i));
            }
        }
    }

    private void OnClickButton() {
        if (verhicle != null) {
            btn_verhicle_add.setVisibility(View.GONE);
        } else
            btn_verhicle_update.setVisibility(View.GONE);

        btn_verhicle_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVerhicle();
            }
        });

        btn_verhicle_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVerhicle();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int id_spinner = parent.getId();
        if (id_spinner == R.id.spinner_brandname) {
            brand_code = adapter_spinner_brand.getItem(position).getCode().toString();
            showShortToast(brand_code);
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
    public void onGetBrandnameData(ArrayList<Brandname> brandNames) {
        brandNames_arr = getBrandNames_arr(brandNames);
        initSpinnerBrandname();
    }

    private ArrayList<Brandname> getBrandNames_arr(ArrayList<Brandname> arrayList) {
        ArrayList<Brandname> brandnames = new ArrayList<Brandname>();
        if (arrayList != null) {
            int total = arrayList.size();
            Log.v("Total brand name: ", String.valueOf(total));
            for (int i = 0; i < total; i++) {
                brandnames.add(i, arrayList.get(i));
                Log.v("Info brand name", brandnames.get(i).toString());
            }
        }

        return brandnames;
    }

    private boolean valid() {

        if (edt_verhicle_name.getText().toString().isEmpty() || edt_verhicle_name.getText().toString() == "") {
            mess = this.getString(R.string.check_verhicle_name);
            return false;
        } else if (edt_verhicle_plate.getText().toString().isEmpty() || edt_verhicle_plate.getText().toString() == "") {
            mess = this.getString(R.string.check_verhicle_plate);
            return false;
        } else if (edt_verhicle_descriptions.getText().toString().isEmpty() || edt_verhicle_descriptions.getText().toString() == "") {
            mess = this.getString(R.string.check_verhicle_des);
            return false;
        }
        return true;
    }

    private int getVerhicleType() {
        int type = 0;
        int id = radioGroup.getCheckedRadioButtonId();
        if (id != -1) {
            if (id == R.id.rdo_oto) {
                type = 1;
            } else if (id == R.id.rdo_xemay) {
                type = 2;
            }
        } else
            type = 1;
        return type;
    }

    private int getFlagDefault() {
        int flag = 0;
        if (chk_verhicle_default.isChecked()) {
            flag = 1;
        } else
            flag = 0;

        return flag;
    }

    private void addVerhicle() {
        if (valid()) {
            Brandname brandname = new Brandname();
            String v_name, v_plate, v_des;
            int v_type, v_flag;
            v_name = edt_verhicle_name.getText().toString();
            v_plate = edt_verhicle_plate.getText().toString();
            v_des = edt_verhicle_descriptions.getText().toString();
            v_type = getVerhicleType();
            v_flag = getFlagDefault();
            verhiclePresenter.addVerhicle(v_name, v_plate, v_des, v_type, v_flag, brand_code);
        } else
            showShortToast(mess);

    }

    private void getDataFromManager() {
        if (validVerhicleModel()) {
            final int id = verhicle.getId();
            final String name = verhicle.getName();
            final String plate_num = verhicle.getPlate_number();
            final String descriptions = verhicle.getDesc();
            final int type = verhicle.getType();
            final int flag_default = verhicle.getFlag_default();
            final String code = verhicle.getBrandname().getCode();
            fill_DataUpdate(id, name, descriptions, plate_num, type, flag_default, code);
        }
    }

    private void fill_DataUpdate(int id, String name, String desc, String plate_number, int type, int flag_default, String code) {

        btn_verhicle_add.setText(this.getString(R.string.update_btn));
        edt_verhicle_name.setText(name);
        edt_verhicle_descriptions.setText(desc);
        edt_verhicle_plate.setText(plate_number);
        getId = id;
        if (type == 1) {
            rdo_oto.setChecked(true);
        } else
            rdo_xemay.setChecked(true);

        if (flag_default == 1) {
            chk_verhicle_default.setChecked(true);
        }
    }

    private void updateVerhicle() {
        String update_name = edt_verhicle_name.getText().toString();
        String update_desc = edt_verhicle_descriptions.getText().toString();
        String update_plate = edt_verhicle_plate.getText().toString();
        int update_type = getVerhicleType();
        int update_flag = getFlagDefault();
        String update_code = brand_code;
        putData2Server(getId, update_name, update_desc, update_plate, update_type, update_flag, update_code);
    }

    private void putData2Server(int id, String name, String desc, String plate_number, int type, int flag_default, String code) {
        verhiclePresenter.updateVerhicle(id, name, desc, plate_number, type, flag_default, code);
    }

    private boolean validVerhicleModel() {
        try {
            verhicle = (Verhicle) getIntent().getSerializableExtra(Constants.VERHICLE_MODEL);
            Log.v("verhicle id", String.valueOf(verhicle.getId()));
        } catch (Exception e) {
//            Log.e("Loi get Verhicle", e.getMessage());
        }

        if (verhicle != null) {
            return true;
        }
        return false;
    }

    @Override
    public void finishActivity() {
        super.finishActivity();
    }

    @Override
    public void putExtra(String key, int id) {
        Intent intent = new Intent();
        intent.putExtra(key, id);
        setResult(VerhicleFragment.RESULT_ADD_VERHICLE, intent);
        finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
