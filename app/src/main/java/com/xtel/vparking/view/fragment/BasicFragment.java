package com.xtel.vparking.view.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.xtel.vparking.view.MyApplication;
import com.xtel.vparking.view.activity.BasicActivity;
import com.xtel.vparking.view.activity.inf.BasicView;

import java.io.Serializable;

/**
 * Created by Lê Công Long Vũ on 12/4/2016.
 */

public abstract class BasicFragment extends Fragment implements BasicView {
    private ProgressDialog progressDialog;
    private Dialog dialog;

    protected final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.xtel.vparking";
    protected final String MARKET = "market://details?id=com.xtel.vparking";

    public BasicFragment() {
    }

    protected void showLongToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void debug(String debuh) {
        Log.d(this.getClass().getSimpleName(), debuh);
    }

    protected void showDialogNotification(String title, String content, final DialogListener dialogListener) {
        final DialogNotification dialogNotification = new DialogNotification(getContext());
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
        progressDialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setCanceledOnTouchOutside(isTouchOutside);
        progressDialog.setCancelable(isCancel);

        if (title != null)
            progressDialog.setTitle(title);
        if (message != null)
            progressDialog.setMessage(message);

        progressDialog.show();
    }

    protected void closeProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    protected void showDialogUpdate() {
        showMaterialDialog(false, false, null, getString(R.string.message_update_version), getString(R.string.cancel), getString(R.string.update_now), new NewDialogListener() {
            @Override
            public void negativeClicked() {
                closeDialog();
                getActivity().finishAffinity();
            }

            @Override
            public void positiveClicked() {
                closeDialog();
                getActivity().finishAffinity();

                try {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL)));
                }
            }
        });
    }

    /*
    * Hiển thị thông báo (chuẩn material)
    * */
    @SuppressWarnings("ConstantConditions")
    protected void showMaterialDialog(boolean isTouchOutside, boolean isCancelable, String title, String message, String negative, String positive, final NewDialogListener dialogListener) {
        dialog = new Dialog(getContext(), R.style.Theme_Transparent);
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

    public void closeDialog() {
        dialog.dismiss();
    }

    //    Khởi chạy fragment giao diện và add vào stack
    protected void replaceFragment(int id, Fragment fragment, String tag) {
        if (getChildFragmentManager().findFragmentByTag(tag) == null) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }
    }

    protected void startActivity(Class clazz) {
        startActivity(new Intent(getActivity(), clazz));
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        getActivity().startActivityForResult(new Intent(getActivity(), clazz), requestCode);
    }

    protected void startActivityForResult(Class clazz, String key, Object object, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtra(key, (Serializable) object);
        debug("" + requestCode);
        getActivity().startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResultWithInteger(Class clazz, String key, int data, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtra(key, data);
        getActivity().startActivityForResult(intent, requestCode);
    }

    protected void startActivityAndFinish(Class clazz) {
        startActivity(new Intent(getActivity(), clazz));
        getActivity().finish();
    }

    @Override
    public void onUpdateVersion() {
        showDialogUpdate();
    }
}