package com.xtel.vparking.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.PlaceModel;
import com.xtel.vparking.model.entity.RESP_Address;
import com.xtel.vparking.view.activity.AddParkingActivity;
import com.xtel.vparking.view.activity.inf.IChooseMapsView;

/**
 * Created by Mr. M.2 on 12/23/2016.
 */

public class ChooseMapsPresenter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private IChooseMapsView view;
    private GoogleApiClient mGoogleApiClient;
    private boolean isViewing = true, isFindLocation = false;

    public ChooseMapsPresenter(IChooseMapsView view) {
        this.view = view;
    }

    public void getData() {
        PlaceModel placeModel = null;

        try {
            placeModel = (PlaceModel) view.getActivity().getIntent().getSerializableExtra(AddParkingActivity.MODEL_FIND);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (placeModel != null)
            view.onGetData(placeModel);
        else
            isFindLocation = true;
    }

    public void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(view.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void getMyLocation() {
        if (!isFindLocation)
            isFindLocation = true;

        if (mGoogleApiClient.isConnected()) {
            if (checkPermission()) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    if (isViewing)
                        view.onGetMyLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
            }
        } else
            mGoogleApiClient.connect();
    }

    public void getAddress(final double lat, final double lng) {
        ParkingModel.getInstanse().getAddressByLatLng(lat, lng, new ResponseHandle<RESP_Address>(RESP_Address.class) {
            @Override
            public void onSuccess(RESP_Address obj) {
                if (obj.getStatus().equals("OK"))
                    view.onGetAddressSucces(lat, lng, obj.getResults().get(0).getFormatted_address());
                else
                    view.onGetAddressError(lat, lng);
            }

            @Override
            public void onError(Error error) {
                view.onGetAddressError(lat, lng);
            }
        });
    }


    public void onStart() {
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isViewing = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isFindLocation)
            getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean checkPermission() {
        try {
            return !(ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
