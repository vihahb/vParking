package com.xtel.vparking.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.callback.NewDialogListener;
import com.xtel.vparking.dialog.DialogNotification;
import com.xtel.vparking.view.activity.inf.BasicView;

import java.io.Serializable;

/**
 * Created by Lê Công Long Vũ on 12/2/2016
 */

public abstract class BasicActivity extends AppCompatActivity implements BasicView {
    private ProgressDialog progressDialog;
    private Dialog dialog;

    protected final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.xtel.vparking";
    protected final String MARKET = "market://details?id=com.xtel.vparking";
    boolean isWaitingForExit = false;

    public BasicActivity() {
    }

    protected void initToolbar(int id, String title) {
        Toolbar toolbar = (Toolbar) findViewById(id);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (title != null)
            actionBar.setTitle(title);
    }

    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showDialogNotification(String title, String content, final DialogListener dialogListener) {
        final DialogNotification dialogNotification = new DialogNotification(this);
        dialogNotification.showDialog(title, content, "OK");
        dialogNotification.setOnButtonClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNotification.hideDialog();
                dialogListener.onClicked(null);
            }
        });
    }

    protected void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message) {
        progressDialog = new ProgressDialog(BasicActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCanceledOnTouchOutside(isTouchOutside);
        progressDialog.setCancelable(isCancel);

        if (title != null)
            progressDialog.setTitle(title);
        if (message != null)
            progressDialog.setMessage(message);

        progressDialog.show();
    }

    protected void closeProgressBar() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @SuppressWarnings("ConstantConditions")
    protected void showDialog(boolean isTouchOutside, boolean isCancelable, String title, String content, String button, View.OnClickListener onClickListener) {
        dialog = new Dialog(BasicActivity.this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_notification);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(isTouchOutside);
        dialog.setCanceledOnTouchOutside(isCancelable);

        TextView txt_title = (TextView) dialog.findViewById(R.id.txt_dialog_notification_title);
        TextView txt_content = (TextView) dialog.findViewById(R.id.txt_dialog_notification_content);
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_dialog_notification_ok);

        if (title == null)
            txt_title.setVisibility(View.GONE);
        else
            txt_title.setText(title);

        if (content == null)
            txt_content.setVisibility(View.GONE);
        else
            txt_content.setText(content);

        if (button == null)
            btn_ok.setVisibility(View.GONE);
        else
            btn_ok.setText(button);

        btn_ok.setOnClickListener(onClickListener);
        dialog.show();
    }

    protected void showDialogUpdate() {
        showMaterialDialog(false, false, null, getString(R.string.message_update_version), getString(R.string.cancel), getString(R.string.update_now), new NewDialogListener() {
            @Override
            public void negativeClicked() {
                closeDialog();
                finishAffinity();
            }

            @Override
            public void positiveClicked() {
                closeDialog();
                finishAffinity();

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL)));
                }
            }
        });
    }

    /*
    * Hiển thị thông báo (chuẩn material)
    * */
    @SuppressWarnings("ConstantConditions")
    protected void showMaterialDialog(boolean isTouchOutside, boolean isCancelable, String title, String message, String negative, String positive, final NewDialogListener dialogListener) {
        dialog = new Dialog(BasicActivity.this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_material);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(isTouchOutside);
        dialog.setCanceledOnTouchOutside(isCancelable);

        TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_txt_title);
        TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_txt_message);
        Button btn_negative = (Button) dialog.findViewById(R.id.dialog_btn_negative);
        Button btn_positive = (Button) dialog.findViewById(R.id.dialog_btn_positive);

        if (title == null)
            txt_title.setVisibility(View.GONE);
        else
            txt_title.setText(title);

        if (message == null)
            txt_message.setVisibility(View.GONE);
        else
            txt_message.setText(message);

        if (negative == null)
            btn_negative.setVisibility(View.GONE);
        else
            btn_negative.setText(negative);

        if (positive == null)
            btn_positive.setVisibility(View.GONE);
        else
            btn_positive.setText(positive);

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogListener.negativeClicked();
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogListener.positiveClicked();
            }
        });

        if (dialog != null)
            dialog.show();
    }

    @SuppressWarnings("ConstantConditions")
    protected void showAskDialog(boolean isTouchOutside, boolean isCancelable, String title, String message,
                                 String negative, String positive, final DialogListener dialogListener) {
        dialog = new Dialog(BasicActivity.this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(isTouchOutside);
        dialog.setCanceledOnTouchOutside(isCancelable);

        TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_txt_title);
        TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_txt_message);
        Button btn_negative = (Button) dialog.findViewById(R.id.dialog_btn_negative);
        Button btn_positive = (Button) dialog.findViewById(R.id.dialog_btn_positive);

        if (title == null)
            txt_title.setVisibility(View.GONE);
        else
            txt_title.setText(title);

        if (txt_message == null)
            txt_message.setVisibility(View.GONE);
        else
            txt_message.setText(message);

        if (negative == null)
            btn_negative.setVisibility(View.GONE);
        else
            btn_negative.setText(negative);

        if (positive == null)
            btn_positive.setVisibility(View.GONE);
        else
            btn_positive.setText(positive);

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogListener.onCancle();
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogListener.onClicked(null);
            }
        });

        if (dialog != null)
            dialog.show();
    }

    public void closeDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    //    Khởi chạy fragment giao diện và add vào stack
    protected void replaceFragment(int id, Fragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }

    protected void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    protected void startActivityForResultWithInteger(Class clazz, String key, int data, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(key, data);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class clazz, String key, Object object, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(key, (Serializable) object);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityAndFinish(Class clazz) {
        startActivity(new Intent(this, clazz));
        finish();
    }

    protected void finishActivity() {
        finish();
    }

    protected void showConfirmExitApp() {
        if (isWaitingForExit) {
            System.exit(0);
        } else {
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    isWaitingForExit = true;
                    showShortToast(getString(R.string.text_back_press_to_exit));

                }

                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    isWaitingForExit = false;
                }
            }.execute();
        }
    }

    @Override
    public void onUpdateVersion() {
        showDialogUpdate();
    }
}