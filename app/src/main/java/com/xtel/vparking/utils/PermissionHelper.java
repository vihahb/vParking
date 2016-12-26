package com.xtel.vparking.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Lê Công Long Vũ on 12/26/2016.
 */

public class PermissionHelper {

    public static boolean checkListPermission(String[] permission, Activity activity, int REQUEST_CODE) {
        boolean isAllow = true;

        for (int i = (permission.length - 1); i >= 0; i--) {
            if (ActivityCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                isAllow = false;
                break;
            }
        }

        if (!isAllow) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS},
                        REQUEST_CODE);
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS},
                        REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return false;
        }
        return true;
    }

    public static boolean checkOnlyPermission(String permission, Activity activity, int REQUEST_CODE) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE);
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return false;
        }
        return true;
    }
}