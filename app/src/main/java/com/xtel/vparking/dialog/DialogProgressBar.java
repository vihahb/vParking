package com.xtel.vparking.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.xtel.vparking.R;

/**
 * Created by Lê Công Long Vũ on 11/10/2016.
 */

public class DialogProgressBar {
    private ProgressDialog progressDialog;

    public DialogProgressBar(Context context, boolean isTouchOutside, boolean isCancel, String title, String message) {
        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCanceledOnTouchOutside(isTouchOutside);
        progressDialog.setCancelable(isCancel);

        if (title != null)
            progressDialog.setTitle(title);

        if (message != null)
            progressDialog.setMessage(message);
    }

    public void updateProgressBar(String title, String message) {
        if (title != null)
            progressDialog.setTitle(title);

        if (message != null)
            progressDialog.setMessage(message);
    }

    public void closeProgressBar() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public void showProgressBar() {
        if (progressDialog != null)
            progressDialog.show();
    }

    public boolean isShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }
}
