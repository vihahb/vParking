package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.presenter.CheckOutPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.CheckOutView;
import com.xtel.vparking.view.fragment.CheckedFragment;

public class CheckOutActivity extends BasicActivity implements View.OnClickListener, CheckOutView {
    private CheckOutPresenter presenter;

    private TextView txt_time, txt_verhicle, txt_parking_name, txt_parking_address, txt_parking_time, txt_parking_price;
    private Button btn_check_out;
    private FloatingActionButton fab_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        initToolbar(R.id.check_out_toolbar, null);
        initView();

        presenter = new CheckOutPresenter(this);
        presenter.getData();
    }

    private void initView() {
        txt_time = (TextView) findViewById(R.id.check_out_txt_time);
        txt_verhicle = (TextView) findViewById(R.id.check_out_txt_verhicle);
        txt_parking_name = (TextView) findViewById(R.id.check_out_txt_parking_name);
        txt_parking_address = (TextView) findViewById(R.id.check_out_txt_parking_address);
        txt_parking_time = (TextView) findViewById(R.id.check_out_txt_parking_time);
        txt_parking_price = (TextView) findViewById(R.id.check_out_txt_parking_price);
        btn_check_out = (Button) findViewById(R.id.check_out_btn);
        fab_view = (FloatingActionButton) findViewById(R.id.check_out_fab);

        fab_view.setOnClickListener(this);
        btn_check_out.setOnClickListener(this);
    }

    @Override
    public void onGetDataSuccess(CheckIn checkIn) {
        txt_time.setText(Constants.convertDate(checkIn.getCheckin_time()));
        txt_verhicle.setText(checkIn.getVehicle().getName() + ": " + checkIn.getVehicle().getPlate_number());
    }

    @Override
    public void onGetDataError() {
        showDialogNotification("Thông báo", "Có lỗi xảy ra\nVui lòng thử lại", new DialogListener() {
            @Override
            public void onClicked(Object object) {
                finish();
            }

            @Override
            public void onCancle() {

            }
        });
    }

    @Override
    public void onGetParkingInfoSuccess(RESP_Parking_Info obj) {
        txt_parking_name.setText(obj.getParking_name());
        txt_parking_address.setText(obj.getAddress());
        txt_parking_time.setText(Constants.getTime(obj.getBegin_time(), obj.getEnd_time()));
        txt_parking_price.setText((obj.getPrices().get(0).getPrice() + "K/h"));
    }

    @Override
    public void onGetParkingInfoError(Error error) {

    }

    @Override
    public void onViewParking(int id) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ID_PARKING, id);
        setResult(CheckedFragment.RESULT_FIND, intent);
        finish();
    }

    @Override
    public void onCheckOutSuccess(final CheckIn checkIn) {
        closeProgressBar();
        showDialogNotification("Thông báo", "Bạn đã check out thành công", new DialogListener() {
            @Override
            public void onClicked(Object object) {
                Intent intent = new Intent();
                intent.putExtra(CheckedFragment.CHECKED_ID, checkIn.getTransaction());
                setResult(CheckedFragment.RESULT_CHECK_OUT, intent);
                finish();
            }

            @Override
            public void onCancle() {

            }
        });
    }

    @Override
    public void onCheckOutError(Error error) {
        closeProgressBar();
        showDialogNotification("Thông báo", JsonParse.getCodeMessage(error.getCode(), "Có lỗi xảy ra"), new DialogListener() {
            @Override
            public void onClicked(Object object) {

            }

            @Override
            public void onCancle() {

            }
        });
    }

    @Override
    public Activity getActivity() {
        return this;
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

        if (id == R.id.check_out_btn) {
            showProgressBar(false, false, null, getString(R.string.doing));
            presenter.checkOut();
        } else if (id == R.id.check_out_fab) {
            presenter.viewParking();
        }
    }
}
