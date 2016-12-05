package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.presenter.CheckInPresenter;
import com.xtel.vparking.view.activity.inf.CheckInView;

public class CheckInActivity extends BasicActivity implements CheckInView {
    private CheckInPresenter presenter;

    private View layout_now, layout_new;
    private TextView txt_now_address, txt_new_title;

    private final int REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        presenter = new CheckInPresenter(this);
        initToolbar(R.id.checkin_toolbar, null);
        initView();
    }

    private void initView() {
        layout_now = (View) findViewById(R.id.checkin_layout_now);
        layout_new = (View) findViewById(R.id.checkin_layout_new);
        txt_now_address = (TextView) findViewById(R.id.checkin_now_address);
        txt_new_title = (TextView) findViewById(R.id.checkin_new_title);
    }

    public void bikeClicked(View view) {
        presenter.startScanQrCode(3, REQUEST_CODE);
    }

    public void carClicked(View view) {
        presenter.startScanQrCode(2, REQUEST_CODE);
    }

    public void truckClicked(View view) {
        presenter.startScanQrCode(4, REQUEST_CODE);
    }

    public void busClicked(View view) {
        presenter.startScanQrCode(5, REQUEST_CODE);
    }





    @Override
    public void onScanSuccess(String content) {
        layout_now.setVisibility(View.VISIBLE);
        txt_now_address.setText(content);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null)
                presenter.checkScanResult(data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
