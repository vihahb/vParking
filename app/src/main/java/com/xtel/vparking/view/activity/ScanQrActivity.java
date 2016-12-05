package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.presenter.ScanQrPresenter;
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
        presenter.checkTransportScan();
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
        mScannerView = new ZXingScannerView(this) {
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
    public void startScanQrCode() {
        layout_gift_code.setVisibility(View.GONE);
        if (!edt_gift_code.getText().toString().isEmpty()) {
            txt_gift_code.setText((getString(R.string.gift_code) + edt_gift_code.getText()));
            edt_gift_code.setText(null);
        }
    }

    @Override
    public void endScanQrCode(String title, final String content) {
        showDialogNotification(title, content, new DialogListener() {
            @Override
            public void onClicked(Object object) {
//                mScannerView.resumeCameraPreview(ScanQrActivity.this);
//                layout_gift_code.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.putExtra(Constants.SCAN_RESULT, content);
                setResult(RESULT_OK, intent);
                finish();
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
//        Toast.makeText(this, "Contents = " + result.getText() +
//                ", Format = " + result.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        presenter.showResult(result.getBarcodeFormat().toString(), result.getText());
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
