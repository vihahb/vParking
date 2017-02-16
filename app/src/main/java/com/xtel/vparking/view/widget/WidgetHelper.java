package com.xtel.vparking.view.widget;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.view.MyApplication;

import java.util.Calendar;

/**
 * Created by Vulcl on 1/17/2017
 */

public class WidgetHelper {
    private static WidgetHelper instance;

    public static WidgetHelper getInstance() {
        if (instance == null) {
            instance = new WidgetHelper();
        }
        return instance;
    }

    public void setImageURL(ImageView view, String url) {
        if (url == null || url.isEmpty())
            return;

        url = url.replace("https", "http").replace("9191", "9190");

        Picasso.with(MyApplication.context)
                .load(url)
                .noPlaceholder()
                .error(R.mipmap.ic_error)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("WidgetHelper", "load ok");
                    }

                    @Override
                    public void onError() {
                        Log.e("WidgetHelper", "load error");
                    }
                });
    }

    public void setSmallImageURL(ImageView view, String url) {
        if (url == null || url.isEmpty())
            return;

        url = url.replace("https", "http").replace("9191", "9190");

        Picasso.with(MyApplication.context)
                .load(url)
                .noPlaceholder()
                .error(R.mipmap.ic_error)
                .fit()
                .centerCrop()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("WidgetHelper", "load ok");
                    }

                    @Override
                    public void onError() {
                        Log.e("WidgetHelper", "load error");
                    }
                });
    }

    public void setImageResource(ImageView view, int resource) {
        view.setImageResource(resource);
    }

    public void setViewBackground(View view, int resource) {
        view.setBackgroundResource(resource);
    }

    public void setEditTextNoResult(EditText view, String content) {
        view.setText(content);
    }

    public void setEditTextWithResult(EditText view, String content, String result) {
        if (content == null || content.isEmpty())
            view.setText(result);
        else
            view.setText(content);
    }

    public void setEditTextWithResult(EditText view, String title, String content, String result) {
        if (content == null || content.isEmpty())
            view.setText((title + result));
        else
            view.setText((title + content));
    }

    public void setEditTextDate(EditText view, long milliseconds) {
        if (milliseconds != 0)
            view.setText(getDate(milliseconds));
    }

    public void setEditTextDate(EditText view, String title, long milliseconds) {
        if (milliseconds != 0)
            view.setText((title + getDate(milliseconds)));
    }

    public void setEditTextDate(EditText view, int day, int month, int year) {
        String mDate, mMonth;

        if (day < 10)
            mDate = "0" + day;
        else
            mDate = String.valueOf(day);

        month = month + 1;
        if (month < 10)
            mMonth = "0" + month;
        else
            mMonth = String.valueOf(month);

        view.setText(day + "-" + mMonth + "-" + year);
    }

    public void setEditTextTime(EditText view, int hour, int minute) {
        String mHour, mMinute;

        if (hour < 10)
            mHour = "0" + hour;
        else
            mHour = String.valueOf(hour);

        if (minute < 10)
            mMinute = "0" + minute;
        else
            mMinute = String.valueOf(minute);

        view.setText(mHour + ":" + mMinute);
    }

    public void setEditTextTime(EditText view, String title, int hour, int minute) {
        String mHour, mMinute;

        if (hour < 10)
            mHour = "0" + hour;
        else
            mHour = String.valueOf(hour);

        if (minute < 10)
            mMinute = "0" + minute;
        else
            mMinute = String.valueOf(minute);

        view.setText(title + mHour + ":" + mMinute);
    }

    public void setEditTextDrawable(EditText view, int position, int resource) {
        switch (position) {
            case 0:
                view.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
                break;
            case 1:
                view.setCompoundDrawablesWithIntrinsicBounds(0, resource, 0, 0);
                break;
            case 2:
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, resource, 0);
                break;
            case 3:
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, resource);
                break;
            default:
                break;
        }
    }

    public void setTextViewNoResult(TextView view, String content) {
        view.setText(content);
    }

    public void setTextViewNoResult(TextView view, String title, String content) {
        view.setText((title + ": " + content));
    }

    public void setTextViewWithResult(TextView view, String content, String result) {
        if (content == null || content.isEmpty())
            view.setText(result);
        else
            view.setText(content);
    }

    public void setTextViewWithResult(TextView view, String title, String content, String result) {
        if (content == null || content.isEmpty())
            view.setText((title + result));
        else
            view.setText((title + content));
    }

    public void setTextViewDate(TextView view, String content, long milliseconds) {
        if (milliseconds == 0)
            view.setText((content + MyApplication.context.getString(R.string.updating)));
        else
            view.setText((content + getDate(milliseconds)));
    }

    public void setTextViewDrawable(TextView view, int position, int resource) {
        switch (position) {
            case 0:
                view.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
                break;
            case 1:
                view.setCompoundDrawablesWithIntrinsicBounds(0, resource, 0, 0);
                break;
            case 2:
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, resource, 0);
                break;
            case 3:
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, resource);
                break;
            default:
                break;
        }
    }

    private String getDate(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        String day;
        if (mDay < 10)
            day = "0" + mDay;
        else
            day = String.valueOf(mDay);

        String month;
        if (mMonth < 10)
            month = "0" + mMonth;
        else
            month = String.valueOf(mMonth);

        return day + "-" + month + "-" + mYear;
    }

    public void setSpinnerGender(Spinner view, int type) {
        view.setSelection(type);
    }
}