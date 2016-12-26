package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.Result;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.presenter.ScanQrPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.ScanQrView;
import com.xtel.vparking.view.adapter.CustomViewFinderView;
import com.xtel.vparking.view.adapter.ScanQrAdapter;

import java.util.ArrayList;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class ScanQrActivity extends BasicActivity implements ZXingScannerView.ResultHandler, ScanQrView {
    private ScanQrPresenter presenter;
    private ZXingScannerView mScannerView;
    private ViewGroup contentFrame;

    private Spinner spinner;
    private LinearLayout layout_gift_code;
    private EditText edt_gift_code;
    private TextView txt_gift_code, txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        presenter = new ScanQrPresenter(this);
        initToolbar(R.id.scanqr_toolbar, null);
        initView();
        initScannerView();
        showLoadingData();
        presenter.getVerhicle();
    }

    private void initView() {
        spinner = (Spinner) findViewById(R.id.scanqr_sp_verhicle);
        contentFrame = (ViewGroup) findViewById(R.id.scanqr_content);
        layout_gift_code = (LinearLayout) findViewById(R.id.scanqr_layout_gift_code);
        edt_gift_code = (EditText) findViewById(R.id.scanqr_edt_gift_code);
        txt_gift_code = (TextView) findViewById(R.id.scanqr_txt_gift_code);
        txt_title = (TextView) findViewById(R.id.scanqr_txt_title);
    }

    private void initScannerView() {
        mScannerView = new ZXingScannerView(getApplicationContext()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    private void showLoadingData() {
        layout_gift_code.setVisibility(View.GONE);
        txt_title.setVisibility(View.GONE);
        txt_gift_code.setText("Đang tải dữ liệu");
    }

    @Override
    public void onGetVerhicleSuccess(ArrayList<Verhicle> arrayList) {
        layout_gift_code.setVisibility(View.VISIBLE);
        txt_title.setVisibility(View.VISIBLE);

        ScanQrAdapter adapter = new ScanQrAdapter(ScanQrActivity.this, arrayList);
        spinner.setAdapter(adapter);

        for (int i = (arrayList.size() - 1); i >= 0; i--) {
            if (arrayList.get(i).getFlag_default() == 1) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    @Override
    public void onGetVerhicleError(Error error) {
        showDialogNotification("Thông báo", "Có lỗi xảy ra, vui lòng thử lại", new DialogListener() {
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
    public void onStartChecking() {
        layout_gift_code.setVisibility(View.GONE);
        txt_gift_code.setText(edt_gift_code.getText().toString());
        edt_gift_code.setText("");
    }

    @Override
    public void onCheckingSuccess() {
        showDialogNotification("Thông báo", "Check in thành công", new DialogListener() {
            @Override
            public void onClicked(Object object) {
                setResult(CheckInActivity.RESULT_CHECK_IN);
                finish();
            }

            @Override
            public void onCancle() {

            }
        });
    }

    @Override
    public void onCheckingError(Error error) {
        layout_gift_code.setVisibility(View.VISIBLE);
        showDialogNotification("Thông báo", JsonParse.getCodeMessage(error.getCode(), "Có lỗi xảy ra"), new DialogListener() {
            @Override
            public void onClicked(Object object) {
                mScannerView.resumeCameraPreview(ScanQrActivity.this);
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
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        if (spinner != null)
            if (NetWorkInfo.isOnline(ScanQrActivity.this))
                presenter.startCheckIn(spinner.getSelectedItemPosition(), edt_gift_code.getText().toString(), result.getText());
            else {
                showShortToast(getString(R.string.no_internet));
                mScannerView.resumeCameraPreview(this);
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}