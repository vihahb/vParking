package com.xtel.vparking.view.activity;

import android.app.Dialog;
import android.content.Intent;
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
import com.xtel.vparking.dialog.DialogNotification;
import com.xtel.vparking.dialog.DialogProgressBar;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public abstract class BasicActivity extends AppCompatActivity {
    private DialogProgressBar dialogProgressBar;
    private Dialog dialog;
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

    protected void debug(String debuh) {
        Log.d(this.getClass().getSimpleName(), debuh);
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
        dialogProgressBar = new DialogProgressBar(BasicActivity.this, isTouchOutside, isCancel, title, message);
        dialogProgressBar.showProgressBar();
    }

    protected void closeProgressBar() {
        if (dialogProgressBar.isShowing())
            dialogProgressBar.closeProgressBar();
    }

    protected void showDialog(String title, String content, String button, View.OnClickListener onClickListener) {
        dialog = new Dialog(BasicActivity.this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_notification);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

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

    public void closeDialog() {
        dialog.dismiss();
    }

    //    Khởi chạy fragment giao diện và add vào stack
    protected void replaceFragment(int id, Fragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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

    protected void startActivityAndFinish(Class clazz) {
        startActivity(new Intent(this, clazz));
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
}
