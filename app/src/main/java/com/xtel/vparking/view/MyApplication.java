package com.xtel.vparking.view;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Lê Công Long Vũ on 11/8/2016.
 */

public class MyApplication extends Application {
    public static Context context;
    public static String PACKAGE_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(getApplicationContext());
        context = this;
        PACKAGE_NAME = context.getPackageName();
        getKeyHash(PACKAGE_NAME);
        Log.v("Pkg name", PACKAGE_NAME);
    }

    private void getKeyHash(String pkg_name) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(pkg_name,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
