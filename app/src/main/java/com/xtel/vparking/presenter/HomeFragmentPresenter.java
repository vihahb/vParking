package com.xtel.vparking.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.LoginModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Find;
import com.xtel.vparking.model.entity.RESP_Parking;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.model.entity.RESP_Router;
import com.xtel.vparking.model.entity.Steps;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.inf.HomeFragmentView;
import com.xtel.vparking.view.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lê Công Long Vũ on 12/4/2016.
 */

public class HomeFragmentPresenter {
    private HomeFragmentView view;

    public HomeFragmentPresenter(HomeFragmentView view) {
        this.view = view;
    }

    public void checkResultSearch() {
        if (HomeActivity.PARKING_ID != -1) {
            view.onSearchParking(HomeActivity.PARKING_ID);
            getParkingInfo(HomeActivity.PARKING_ID);
        }

        HomeActivity.PARKING_ID = -1;
    }

    public void checkFindOption(Find find_option) {
        if (find_option.getType() == -1 && find_option.getPrice_type() == -1 && find_option.getPrice() == -1 &&
                find_option.getBegin_time() == "" && find_option.getEnd_time() == "") {
            view.onCheckFindOptionSuccess(R.mipmap.ic_filter);
        } else
            view.onCheckFindOptionSuccess(R.mipmap.ic_edit_filter);
    }

    public LatLng getMyLocation() {
        if (HomeFragment.mGoogleApiClient.isConnected()) {
            if (checkPermission()) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(HomeFragment.mGoogleApiClient);
                if (mLastLocation != null) {
                    view.onGetMyLocationSuccess(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
            }
        } else
            HomeFragment.mGoogleApiClient.connect();
        return null;
    }

    public void getParkingInfo(final int id) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + id;
        String session = LoginModel.getInstance().getSession();

        ParkingModel.getInstanse().getParkingInfo(url, session, new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                view.onGetParkingInfoSuccess(obj);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionParkingInfo(id);
                else
                    view.onGetParkingInfoError(error);
            }
        });
    }

    private void getNewSessionParkingInfo(final int id) {
        Log.e("home", "old: " + LoginModel.getInstance().getSession());
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                getParkingInfo(id);
                Log.e("home", "new: " + LoginModel.getInstance().getSession());
            }

            @Override
            public void onError() {
                view.onGetParkingInfoError(new Error(2, "ERROR", view.getActivity().getString(R.string.error_session_invalid)));
            }
        });
    }

    public void getParkingAround(double lat, double lng, Find find) {
        String url = Constants.SERVER_PARKING + Constants.PARKING_FIND + Constants.PARKING_LAT + lat + Constants.PARKING_LNG + lng;
        if (find.getPrice() != -1)
            url += Constants.PARKING_PRICE + find.getPrice();
        if (find.getPrice_type() != -1)
            url += Constants.PARKING_PRICE_TYPE + find.getPrice_type();
        if (find.getType() != -1)
            url += Constants.PARKING_TYPE + find.getType();
        if (!find.getBegin_time().isEmpty())
            url += Constants.PARKING_BEGIN_TIME + find.getBegin_time();
        if (!find.getEnd_time().isEmpty())
            url += Constants.PARKING_END_TIME + find.getEnd_time();

        Log.e("home", "url search " + url);

        ParkingModel.getInstanse().getParkingAround(url, new ResponseHandle<RESP_Parking>(RESP_Parking.class) {
            @Override
            public void onSuccess(RESP_Parking obj) {
                view.onGetParkingAroundSuccess(obj.getData());
            }

            @Override
            public void onError(Error error) {
                view.onGetParkingAroundError(error);
            }
        });
    }

    private boolean checkPermission() {
        try {
            return !(ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getPolyLine(final double from_lat, final double from_lng, double to_lat, double to_lng) {
        String url = Constants.POLY_HTTP + from_lat + "," + from_lng + Constants.POLY_DESTINATION + to_lat + "," + to_lng;
        ParkingModel.getInstanse().getPolyLine(url, new ResponseHandle<RESP_Router>(RESP_Router.class) {
            @Override
            public void onSuccess(RESP_Router obj) {
                if (obj != null) {
                    LatLng latLng = new LatLng(from_lat, from_lng);
                    PolylineOptions polylineOptions = getPolylineOption(obj.getRoutes().get(0).getLegs().get(0).getSteps());

                    if (polylineOptions != null)
                        view.onGetPolylineSuccess(latLng, polylineOptions);
                    else
                        view.onGetPolylineError("Không thể đẫn đường");
                }
            }

            @Override
            public void onError(Error error) {
                view.onGetPolylineError("Không thể đẫn đường");
            }
        });
    }

    private PolylineOptions getPolylineOption(ArrayList<Steps> steps) {
        try {
            PolylineOptions polylineOptions = new PolylineOptions();

            for (int i = 0; i < steps.size(); i++) {
                List<LatLng> poly = Constants.decodePoly(steps.get(i).getPolyline().getPoints());

                for (int j = 0; j < poly.size(); j++) {
                    polylineOptions.add(poly.get(j));
                }
            }

            return polylineOptions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}