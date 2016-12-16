package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.presenter.ScanQrPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.ScanQrView;
import com.xtel.vparking.view.adapter.CustomViewFinderView;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class ScanQrActivity extends BasicActivity implements ZXingScannerView.ResultHandler, ScanQrView {
    private ScanQrPresenter presenter;
    private ZXingScannerView mScannerView;
    private ViewGroup contentFrame;

    private LinearLayout layout_gift_code;
    private EditText edt_gift_code;
    private TextView txt_gift_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        presenter = new ScanQrPresenter(this);
        presenter.getData();

        initView();
        initScannerView();
    }

    private void initView() {
        contentFrame = (ViewGroup) findViewById(R.id.scanqr_content);
        layout_gift_code = (LinearLayout) findViewById(R.id.scanqr_layout_gift_code);
        edt_gift_code = (EditText) findViewById(R.id.scanqr_edt_gift_code);
        txt_gift_code = (TextView) findViewById(R.id.scanqr_txt_gift_code);
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

    @Override
    public void onSetupToolbar(String title) {
        initToolbar(R.id.scanqr_toolbar, title);
    }

    @Override
    public void onGetDataError() {
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
        if (!edt_gift_code.getText().toString().isEmpty()) {
            txt_gift_code.setText(edt_gift_code.getText().toString());
            edt_gift_code.setText("");
        }
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
        if (NetWorkInfo.isOnline(ScanQrActivity.this))
            presenter.startCheckIn(edt_gift_code.getText().toString(), result.getText());
        else
            showShortToast(getString(R.string.no_internet));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}