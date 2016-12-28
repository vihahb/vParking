package com.xtel.vparking.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.view.MyApplication;

import org.json.JSONObject;

/**
 * Created by Lê Công Long Vũ on 11/9/2016.
 */

public class JsonParse {

    public static Error checkError(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject error = jsonObject.getJSONObject(Constants.JSON_ERROR);

            Error errorModel = new Error();
            errorModel.setCode(error.getInt(Constants.JSON_CODE));
            errorModel.setType(error.getString(Constants.JSON_TYPE));
            errorModel.setMessage(error.getString(Constants.JSON_MESSAGE));

            return errorModel;
        } catch (Exception e) {
            Log.e("parse_error", e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkJsonObject(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            return true;
        } catch (Exception e) {
            Log.e("Loi_check_json", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void getCodeError(Context activity, View view, int code, String content) {
        if (code == 3) {
            if (view != null)
                Snackbar.make(view, activity.getString(R.string.error_no_permission), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.error_no_permission), Toast.LENGTH_SHORT).show();
        } else if (code == 4) {
            if (view != null)
                Snackbar.make(view, activity.getString(R.string.error_system), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.error_system), Toast.LENGTH_SHORT).show();
        } else if (code == 1001) {
            if (view != null)
                Snackbar.make(view, activity.getString(R.string.error_no_parking), Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.error_no_parking), Toast.LENGTH_SHORT).show();
        } else if (code == 3002) {
            if (view != null)
                Snackbar.make(view, "Phương tiện không tồn tại", Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, "Phương tiện không tồn tại", Toast.LENGTH_SHORT).show();
        } else if (code == 3003) {
            if (view != null)
                Snackbar.make(view, "Phương tiện đã check in bởi 1 user khác", Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, "Phương tiện đã check in bởi 1 user khác", Toast.LENGTH_SHORT).show();
        } else if (code == 3004) {
            if (view != null)
                Snackbar.make(view, "Bạn chưa check in hoạc đã check out", Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, "Bạn chưa check in hoạc đã check out", Toast.LENGTH_SHORT).show();
        } else if (code == 3006) {
            if (view != null)
                Snackbar.make(view, "Ảnh không tồn tại", Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, "Ảnh không tồn tại", Toast.LENGTH_SHORT).show();
        } else if (code == 3007) {
            if (view != null)
                Snackbar.make(view, "Giá không tồn tại", Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, "Giá không tồn tại", Toast.LENGTH_SHORT).show();
        } else {
            if (view != null)
                Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getCodeMessage(int code, String content) {
        if (code == 2) {
            return MyApplication.context.getString(R.string.error_session_invalid);
        } else if (code == 3) {
            return MyApplication.context.getString(R.string.error_no_permission);
        } else if (code == 4) {
            return MyApplication.context.getString(R.string.error_system);
        } else if (code == 1001) {
            return MyApplication.context.getString(R.string.error_no_parking);
        } else if (code == 3002) {
            return "Phương tiện không tồn tại";
        } else if (code == 3003) {
            return "Phương tiện đã check in bởi 1 user khác";
        } else if (code == 3004) {
            return "Bạn chưa check in hoạc đã check out";
        } else if (code == 3006) {
            return "Ảnh không tồn tại";
        } else if (code == 3007) {
            return "Giá không tồn tại";
        } else if (code == 5555) {
            return "Bạn không có xe nào";
        } else if (code == 0) {
            return "Vui lòng chọn phương tiện";
        } else
            return content;
    }
}