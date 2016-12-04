package com.xtel.vparking.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtel.vparking.R;

/**
 * Created by Lê Công Long Vũ on 10/26/2016.
 */

public class DialogNotification {
    private Context context;
    private static Dialog dialog;
    private Button btn_ok;

    public DialogNotification(Context context) {
        this.context = context;
    }

    //    Nếu không có giá trị thì truyền vào null
    //    VD: không có title thì truyền vào title là null
    public void showDialog(String title, String content, String button) {
        dialog = new Dialog(context, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_notification);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView txt_title = (TextView) dialog.findViewById(R.id.txt_dialog_notification_title);
        TextView txt_content = (TextView) dialog.findViewById(R.id.txt_dialog_notification_content);
        btn_ok = (Button) dialog.findViewById(R.id.btn_dialog_notification_ok);

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

        dialog.show();
    }

    public void setOnButtonClicked(View.OnClickListener onClickListener) {
        btn_ok.setOnClickListener(onClickListener);
    }

    public void hideDialog() {
        if (dialog != null)
            dialog.dismiss();
    }
}