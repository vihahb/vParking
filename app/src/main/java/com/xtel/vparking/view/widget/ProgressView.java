package com.xtel.vparking.view.widget;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xtel.vparking.R;

/**
 * Created by Lê Công Long Vũ on 11/1/2016.
 */

public class ProgressView {
    private View view;

    private LinearLayout layout_data, layout_pro;
    private ImageView imageView;
    private TextView textView_data, textView_pro;
    private Button button;
    private ProgressBar progressBar;

    public ProgressView(Activity activity, View view) {

        if (view == null) {
            layout_data = (LinearLayout) activity.findViewById(R.id.layout_progress_view_data);
            layout_pro = (LinearLayout) activity.findViewById(R.id.layout_progress_view_pro);
            imageView = (ImageView) activity.findViewById(R.id.img_progress_view_data);
            textView_data = (TextView) activity.findViewById(R.id.txt_progress_view_data);
            textView_pro = (TextView) activity.findViewById(R.id.txt_progress_view_pro);
            button = (Button) activity.findViewById(R.id.btn_progress_view_data);
            progressBar = (ProgressBar) activity.findViewById(R.id.pro_progress_view_pro);
        } else {
            layout_data = (LinearLayout) view.findViewById(R.id.layout_progress_view_data);
            layout_pro = (LinearLayout) view.findViewById(R.id.layout_progress_view_pro);
            imageView = (ImageView) view.findViewById(R.id.img_progress_view_data);
            textView_data = (TextView) view.findViewById(R.id.txt_progress_view_data);
            textView_pro = (TextView) view.findViewById(R.id.txt_progress_view_pro);
            button = (Button) view.findViewById(R.id.btn_progress_view_data);
            progressBar = (ProgressBar) view.findViewById(R.id.pro_progress_view_pro);
        }
    }

    public void setUpWithView(View view) {
        this.view = view;
    }

    public void initData(int imageView, String textViewData, String button, String textViewPro, int color) {
        if (imageView == -1)
            this.imageView.setVisibility(View.GONE);
        else
            this.imageView.setImageResource(imageView);

        if (textViewData == null)
            this.textView_data.setVisibility(View.GONE);
        else
            this.textView_data.setText(textViewData);

        if (button == null)
            this.button.setVisibility(View.GONE);
        else
            this.button.setText(button);

        if (textViewPro == null)
            this.textView_pro.setVisibility(View.GONE);
        else
            this.textView_pro.setText(textViewPro);

        if (color != -1)
            this.progressBar.getIndeterminateDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void updateData(int imageView, String textView, String button) {
        if (imageView == -1)
            this.imageView.setVisibility(View.GONE);
        else
            this.imageView.setImageResource(imageView);

        if (textView == null)
            this.textView_data.setVisibility(View.GONE);
        else
            this.textView_data.setText(textView);

        if (button == null)
            this.button.setVisibility(View.GONE);
        else
            this.button.setText(button);
    }

    public void showData() {
        if (view != null && view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        if (layout_pro.getVisibility() == View.VISIBLE)
            layout_pro.setVisibility(View.GONE);
        layout_data.setVisibility(View.VISIBLE);
    }

    public void showProgressbar() {
        if (view != null && view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        if (layout_data.getVisibility() == View.VISIBLE)
            layout_data.setVisibility(View.GONE);
        layout_pro.setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (layout_data.getVisibility() == View.VISIBLE)
            layout_data.setVisibility(View.GONE);
        if (layout_pro.getVisibility() == View.VISIBLE)
            layout_pro.setVisibility(View.GONE);
        if (view != null && layout_pro.getVisibility() == View.GONE)
            view.setVisibility(View.VISIBLE);
    }

    public void setProgressViewClick(View.OnClickListener onClickListener) {
//        this.listener = listener;
        button.setOnClickListener(onClickListener);
    }
}