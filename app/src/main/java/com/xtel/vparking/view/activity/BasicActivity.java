package com.xtel.vparking.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import vn.xtel.quanlybaido.dialog.DialogProgressBar;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public abstract class BasicActivity extends AppCompatActivity {
    private DialogProgressBar dialogProgressBar;

    public BasicActivity() {
    }

    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void bebug(String debuh) {
        Log.d(this.getClass().getSimpleName(), debuh);
    }

    protected void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message) {
        dialogProgressBar = new DialogProgressBar(BasicActivity.this, isTouchOutside, isCancel, title, message);
        dialogProgressBar.showProgressBar();
    }

    protected void closeProgressBar() {
        if (dialogProgressBar.isShowing())
        dialogProgressBar.closeProgressBar();
    }

    protected void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    protected void startActivityAndFinish(Class clazz) {
        startActivity(new Intent(this, clazz));
        finish();
    }
}
