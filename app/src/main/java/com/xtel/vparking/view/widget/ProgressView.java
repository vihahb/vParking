package com.xtel.vparking.view.widget;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
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
    private LinearLayout layout_data;
    private ImageView imageView;
    private TextView textView_data;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ProgressView(Activity activity, View view) {
        if (view == null) {
            layout_data = (LinearLayout) activity.findViewById(R.id.layout_progress_view_data);
            imageView = (ImageView) activity.findViewById(R.id.img_progress_view_data);
            textView_data = (TextView) activity.findViewById(R.id.txt_progress_view_data);
            swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_progress_view);
        } else {
            layout_data = (LinearLayout) view.findViewById(R.id.layout_progress_view_data);
            imageView = (ImageView) view.findViewById(R.id.img_progress_view_data);
            textView_data = (TextView) view.findViewById(R.id.txt_progress_view_data);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_progress_view);
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
        else {
            if (button != null)
                textViewData += "\n" + button;
            this.textView_data.setText(textViewData);
        }
    }

    public void updateData(int imageView, String textView, String button) {
        if (imageView == -1)
            this.imageView.setVisibility(View.GONE);
        else
            this.imageView.setImageResource(imageView);

        if (textView == null)
            this.textView_data.setVisibility(View.GONE);
        else {
            if (button != null)
                textView += "\n" + button;
            this.textView_data.setText(textView);
        }
    }

    public void showData() {
        if (view != null && view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        if (layout_data.getVisibility() == View.VISIBLE)
            layout_data.setVisibility(View.GONE);
        layout_data.setVisibility(View.VISIBLE);
    }

    public void hideData() {
        if (view != null && view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        if (layout_data.getVisibility() == View.VISIBLE)
            layout_data.setVisibility(View.GONE);
    }

    public void show() {
        if (layout_data.getVisibility() == View.GONE)
            layout_data.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.getVisibility() == View.GONE)
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (view != null)
            view.setVisibility(View.GONE);
    }

    public void hide() {
        if (layout_data.getVisibility() == View.VISIBLE)
            layout_data.setVisibility(View.GONE);
        if (swipeRefreshLayout.getVisibility() == View.VISIBLE)
            swipeRefreshLayout.setVisibility(View.GONE);
        if (view != null)
            view.setVisibility(View.VISIBLE);
    }

    public void onLayoutClicked(View.OnClickListener onClickListener) {
        layout_data.setOnClickListener(onClickListener);
    }

    public void onRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    public void setRefreshing(boolean refresh) {
        swipeRefreshLayout.setRefreshing(refresh);
    }

    public void disableSwipe() {
        swipeRefreshLayout.setEnabled(false);
    }

    public void onSwipeLayoutPost(Runnable runnable) {
        swipeRefreshLayout.post(runnable);
    }
}