package com.xtel.vparking.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.dialog.BottomSheet;
import com.xtel.vparking.dialog.DialogProgressBar;
import com.xtel.vparking.model.FindModel;
import com.xtel.vparking.model.ParkingInfoModel;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.MapModel;
import com.xtel.vparking.model.entity.MarkerModel;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.FindAdvancedActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 11/15/2013.
 */

public class HomeFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, LocationListener, View.OnClickListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener {

    private final String TAG = "HomeFragment";
    private GoogleMap mMap, mMap_bottom;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ArrayList<MarkerModel> markerList;
    private FloatingActionButton fab_thongbao, fab_location;
    private SupportPlaceAutocompleteFragment autocompleteFragment;

    public BottomSheetBehavior bottomSheetBehavior;
    private boolean isFindMyLocation, isScrollDown, isCanLoadMap = true;
    private int isLoadNewParking = 0;
    private Gson gson;

    private Marker pickMarker;
    private Polyline polyline;

    private NestedScrollView nestedScrollView;
    private BottomSheet dialogBottomSheet;
    //    private DialogParkingDetail dialogParkingDetail;
    private ParkingInfoModel parkingInfoModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gson = new Gson();

        createLocationRequest();
        initGoogleMap();
        initWidget(view);
        initSearchView();
        initBottomSheet(view);
        initGooogleBottomSheet();
        initBottomSheetView(view);
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_parking);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        markerList = new ArrayList<>();
    }

    private void initWidget(View view) {
        LinearLayout layout_timkiem = (LinearLayout) view.findViewById(R.id.layout_parking_timkiem);
        fab_thongbao = (FloatingActionButton) view.findViewById(R.id.fab_parking_thongbao);
        fab_location = (FloatingActionButton) view.findViewById(R.id.fab_parking_location);

        layout_timkiem.setOnClickListener(this);
        fab_location.setOnClickListener(this);
    }

    private void initSearchView() {
        autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if (mMap != null) {
//                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 15));
                    if (isCanLoadMap)
                        new GetParkingAround().execute(String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude), null, null, null, null);
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
//                Log.e("ParkingStatus", "An error occurred: " + status);
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void initBottomSheet(View view) {
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.bottom_sheet_home);

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet_home));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    Log.e(TAG, "STATE_SETTLING");
                    if (isScrollDown)
                        dialogBottomSheet.showHeader();
                    else
                        dialogBottomSheet.hideHeader();
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    Log.e(TAG, "STATE_DRAGGING");
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    showFloatingActionButton(fab_location);
                    showFloatingActionButton(fab_thongbao);

                    nestedScrollView.scrollTo(0, 0);
                    mMap_bottom.clear();
                    dialogBottomSheet.clearData();
                    dialogBottomSheet.showHeader();
                    parkingInfoModel = null;
                    isLoadNewParking = 0;

                    if (!isCanLoadMap)
                        closeGuid();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    nestedScrollView.scrollTo(0, 0);
                    dialogBottomSheet.showHeader();
                    isScrollDown = false;
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    dialogBottomSheet.hideHeader();
                    isScrollDown = true;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private void hideFloatingActionButton(View view) {
        ViewCompat.animate(view).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                    }

                    public void onAnimationCancel(View view) {
                    }

                    public void onAnimationEnd(View view) {
                        view.setVisibility(View.GONE);
                    }
                }).start();
    }

    private void showFloatingActionButton(View view) {
        view.setVisibility(View.VISIBLE);
        ViewCompat.animate(view).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                .start();
    }

    private void initGooogleBottomSheet() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_dialog_bottom_sheet);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap_bottom = googleMap;

                if (checkPermission()) {
                    mMap_bottom.getUiSettings().setMapToolbarEnabled(false);
                    mMap_bottom.setMyLocationEnabled(true);
                    mMap_bottom.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
        });
    }

    private void initBottomSheetView(View view) {
        dialogBottomSheet = new BottomSheet(getContext(), view, getChildFragmentManager());

        dialogBottomSheet.onContentCliecked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBottomSheet.hideHeader();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        dialogBottomSheet.onGuidClicked(new DialogListener() {
            @Override
            public void onClicked(Object object) {
                if (parkingInfoModel != null) {
                    if (mGoogleApiClient.isConnected()) {
                        if (checkPermission()) {
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null && mMap != null) {
                                new GetPolyline().execute(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                        parkingInfoModel.getLat(), parkingInfoModel.getLng());
                            }
                        }
                    } else
                        mGoogleApiClient.connect();
                }
            }

            @Override
            public void onCancle() {

            }
        });

        dialogBottomSheet.onCloseClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeGuid();
            }
        });
    }

    private void closeGuid() {
        isCanLoadMap = true;
        mMap.clear();
        dialogBottomSheet.changeCloseToFavorite();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        new GetParkingAroundCenter().execute(String.valueOf(mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude),
                String.valueOf(mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude), null, null, null, null);
    }

    private void showDialogParkingDetail() {
        hideFloatingActionButton(fab_location);
        hideFloatingActionButton(fab_thongbao);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        dialogBottomSheet.initData(parkingInfoModel);

        if (parkingInfoModel.getStatus() == 0) {
            mMap_bottom.addMarker(new MarkerOptions()
                    .position(new LatLng(parkingInfoModel.getLat(), parkingInfoModel.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));
        } else {
            mMap_bottom.addMarker(new MarkerOptions()
                    .position(new LatLng(parkingInfoModel.getLat(), parkingInfoModel.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));
        }
        mMap_bottom.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingInfoModel.getLat(), parkingInfoModel.getLng()), 15));
    }

    private boolean checkPermission() {
        try {
            return !(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("pk_map", "map ready");
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.026529, 105.831361), 15));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);

        if (checkPermission()) {
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onMapLongClick(LatLng latLng) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_marker_my_location);
        Bitmap small_bitmap = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 40, 40, true);

        if (pickMarker != null)
            pickMarker.remove();

        pickMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(small_bitmap)));

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        if (isCanLoadMap)
            new GetParkingAround().execute(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), null, null, null, null);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (isCanLoadMap) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mMap_bottom.clear();
                dialogBottomSheet.clearData();
            }

            for (int i = 0; i < markerList.size(); i++) {
                Log.e("pk_choose_marker", "check " + markerList.get(i).getMarker().getId() + "     " + marker.getId());
                if (markerList.get(i).getMarker().getId().equals(marker.getId())) {
                    final int finalI = i;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("pk_choose_marker", "ok");
                            new GetParkingInfo().execute(String.valueOf(markerList.get(finalI).getId()));
                        }
                    }, 200);
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isCanLoadMap)
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onCameraIdle() {
        if (isCanLoadMap) {
            if (isLoadNewParking == 0) {
                isLoadNewParking++;
            } else if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            new GetParkingAroundCenter().execute(String.valueOf(mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude),
                    String.valueOf(mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude), null, null, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.layout_parking_timkiem) {
            startActivityForResult(new Intent(getContext(), FindAdvancedActivity.class), Constants.FIND_ADVANDCED_RQ);
        } else if (id == R.id.fab_parking_location) {
            if (pickMarker != null)
                pickMarker.remove();

            if (mGoogleApiClient.isConnected()) {
                if (checkPermission()) {
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null && mMap != null) {
//                        mMap.clear();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
                        if (isCanLoadMap)
                            new GetParkingAround().execute(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), null, null, null, null);
                    }
                }
            } else
                mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (!isFindMyLocation) {
                autocompleteFragment.setBoundsBias(new LatLngBounds(
                        new LatLng(-33.880490, 151.184363),
                        new LatLng(-33.858754, 151.229596)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                if (isCanLoadMap)
                    new GetParkingAround().execute(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), null, null, null, null);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!isFindMyLocation)
            if (checkPermission()) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null && mMap != null) {
//                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
                    if (isCanLoadMap)
                        new GetParkingAround().execute(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), null, null, null, null);
                }
            }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected())
            try {
                startLocationUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onStop() {
        try {
            stopLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    protected void startLocationUpdates() {
        if (checkPermission())
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.FIND_ADVANDCED_RQ && resultCode == Constants.FIND_ADVANDCED_RS) {
            if (mGoogleApiClient.isConnected()) {
                if (checkPermission()) {
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null && mMap != null) {
                        FindModel findModel = (FindModel) data.getExtras().getSerializable(Constants.FIND_MODEL);

                        if (findModel != null) {
                            Toast.makeText(getContext(), "find", Toast.LENGTH_SHORT).show();
//                            mMap.clear();
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
                            if (isCanLoadMap)
                                new GetParkingAround().execute(
                                        String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()),
                                        String.valueOf(findModel.getMoney()), String.valueOf(findModel.getType()), null, null);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.error_find_advanced), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else
                mGoogleApiClient.connect();
        }
    }

    private void setOnCameraChangeListener() {
        if (mMap != null)
            mMap.setOnCameraIdleListener(this);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void clearMarker(final int possition) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (possition == -1)
                        return;

                    for (int i = possition; i > 0; i--) {
                        markerList.get(i).getMarker().remove();
                        markerList.remove(i);
                    }
//        }
                    Log.e("pk_clear_marker", "done: " + markerList.size());
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetParkingAround extends AsyncTask<String, Void, String> {
        private LatLng latLng;

        @Override
        protected void onPreExecute() {
            if (!isFindMyLocation)
                isFindMyLocation = true;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            try {
                String url = Constants.SERVER_PARKING + Constants.PARKING_FIND +
                        Constants.PARKING_LAT + params[0] +
                        Constants.PARKING_LNG + params[1];
                if (params[2] != null)
                    url += Constants.PARKING_PRICE + params[2];
                if (params[3] != null)
                    url += Constants.PARKING_TYPE + params[3];
                if (params[4] != null)
                    url += Constants.PARKING_BEGIN_TIME + params[4];
                if (params[5] != null)
                    url += Constants.PARKING_END_TIME + params[5];

                Log.e("pk_url", url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                latLng = new LatLng(Double.parseDouble(params[0]), Double.parseDouble(params[1]));

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("pk_loi", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            setOnCameraChangeListener();

            if (latLng != null)
                if (s != null) {
                    Error error = JsonParse.checkError(s);
                    if (error != null) {
                        JsonParse.getCodeError(getActivity(), null, error.getCode(), "Không thể tìm bãi đỗ");
                    } else {
                        ArrayList<ParkingModel> arrayList = JsonParse.getAllParking(s);
//                        markerList.clear();

                        if (arrayList != null) {
                            if (arrayList.size() != 0) {
                                int possition = markerList.size() - 1;

                                for (int i = 0; i < arrayList.size(); i++) {

                                    if (arrayList.get(i).getStatus() == 0) {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(arrayList.get(i).getLat(), arrayList.get(i).getLng()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));

                                        markerList.add(new MarkerModel(marker, arrayList.get(i).getId(), arrayList.get(i).getLat(), arrayList.get(i).getLng()));
                                    } else {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(arrayList.get(i).getLat(), arrayList.get(i).getLng()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));

                                        markerList.add(new MarkerModel(marker, arrayList.get(i).getId(), arrayList.get(i).getLat(), arrayList.get(i).getLng()));
                                    }
                                }

                                clearMarker(possition);
                                arrayList.clear();
                            } else {
                                Toast.makeText(getContext(), "Không tìm thấy bãi đỗ nào", Toast.LENGTH_SHORT).show();
                                clearMarker(-2);
                            }
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy bãi đỗ nào", Toast.LENGTH_SHORT).show();
                            clearMarker(-2);
                        }
                    }
                }
        }
    }

    private class GetParkingAroundCenter extends AsyncTask<String, Void, String> {
        private LatLng latLng;

        @Override
        protected void onPreExecute() {
            if (!isFindMyLocation)
                isFindMyLocation = true;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            try {
                String url = Constants.SERVER_PARKING + Constants.PARKING_FIND +
                        Constants.PARKING_LAT + params[0] +
                        Constants.PARKING_LNG + params[1];
                if (params[2] != null)
                    url += Constants.PARKING_PRICE + params[2];
                if (params[3] != null)
                    url += Constants.PARKING_TYPE + params[3];
                if (params[4] != null)
                    url += Constants.PARKING_BEGIN_TIME + params[4];
                if (params[5] != null)
                    url += Constants.PARKING_END_TIME + params[5];

                Log.e("pk_url", url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                latLng = new LatLng(Double.parseDouble(params[0]), Double.parseDouble(params[1]));

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("pk_loi", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            setOnCameraChangeListener();

            if (latLng != null)
                if (s != null) {
                    Error error = JsonParse.checkError(s);
                    if (error == null) {
                        ArrayList<ParkingModel> arrayList = JsonParse.getAllParking(s);
//                        markerList.clear();

                        if (arrayList != null) {
                            if (arrayList.size() != 0) {
                                int possition = markerList.size() - 1;

                                for (int i = 0; i < arrayList.size(); i++) {

                                    if (arrayList.get(i).getStatus() == 0) {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(arrayList.get(i).getLat(), arrayList.get(i).getLng()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));

                                        markerList.add(new MarkerModel(marker, arrayList.get(i).getId(), arrayList.get(i).getLat(), arrayList.get(i).getLng()));
                                    } else {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(arrayList.get(i).getLat(), arrayList.get(i).getLng()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));

                                        markerList.add(new MarkerModel(marker, arrayList.get(i).getId(), arrayList.get(i).getLat(), arrayList.get(i).getLng()));
                                    }
                                }

                                clearMarker(possition);
                                arrayList.clear();
                            }
                        }
                    }
                }
        }
    }

    private DialogProgressBar dialogProgressBar;

    private class GetParkingInfo extends AsyncTask<String, Void, String> {
        private String id;

        @Override
        protected void onPreExecute() {
            isLoadNewParking = 0;
            if (dialogProgressBar == null)
                dialogProgressBar = new DialogProgressBar(getContext(), false, false, null, getString(R.string.parking_get_data));
            if (!dialogProgressBar.isShowing())
                dialogProgressBar.showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            this.id = params[0];

            try {
                String url = Constants.SERVER_PARKING + Constants.PARKING_INFO + params[0];
                String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);

                Log.e("pk_in_url", url + "     " + session);
                Request request = new Request.Builder()
                        .url(url)
                        .header(Constants.JSON_SESSION, session)
                        .build();
                Response response = client.newCall(request).execute();


                return response.body().string();
            } catch (Exception e) {
                Log.e("pk_loi", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                Error error = JsonParse.checkError(s);
                if (error != null) {
                    if (error.getCode() == 2) {
                        getNewSessionParkingInfo(id);
                    } else {
                        dialogProgressBar.closeProgressBar();
                        JsonParse.getCodeError(getContext(), null, error.getCode(), getString(R.string.error_get_parking));
                    }
                } else {
                    dialogProgressBar.closeProgressBar();
                    parkingInfoModel = gson.fromJson(s, new TypeToken<ParkingInfoModel>() {
                    }.getType());

                    Log.e("pk_parking_id", "null k:" + parkingInfoModel.getId());
                    showDialogParkingDetail();
                }
            }
        }
    }

    private void getNewSessionParkingInfo(final String id) {
        Toast.makeText(getContext(), "Lấy phiên mới", Toast.LENGTH_SHORT).show();
        GetNewSession.getNewSession(getContext(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                new GetParkingInfo().execute(id);
            }

            @Override
            public void onError() {
                dialogProgressBar.closeProgressBar();
                Toast.makeText(getContext(), getString(R.string.error_session_invalid), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class GetPolyline extends AsyncTask<Double, Void, Void> {
        private PolylineOptions polylineOptions;
        private DialogProgressBar dialogProgressBar_2;
        private LatLng latLng;

        @Override
        protected void onPreExecute() {
            dialogProgressBar_2 = new DialogProgressBar(getContext(), false, false, null, getString(R.string.parking_get_data));
            dialogProgressBar_2.showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Double... params) {
            try {
                latLng = new LatLng(params[0], params[1]);
                String url = Constants.POLY_HTTP + params[0] + "," + params[1] +
                        Constants.POLY_DESTINATION + params[2] + "," + params[3];

                Log.e("ct_url", url);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                String content = response.body().string();

                polylineOptions = new PolylineOptions();

                if (!content.isEmpty()) {
                    MapModel parseMap = gson.fromJson(content, MapModel.class);

                    ArrayList<MapModel.Routers.Legs.Steps> steps = parseMap.getRouters().get(0).getLegses().get(0).getStepses();

                    for (int i = 0; i < steps.size(); i++) {
                        List<LatLng> poly = Constants.decodePoly(steps.get(i).getPolyline().getPoints());

                        for (int j = 0; j < poly.size(); j++) {
                            polylineOptions.add(poly.get(j));
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("ct_loi_poly", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (polylineOptions != null) {
                isCanLoadMap = false;

                mMap.clear();
                dialogBottomSheet.changeFavoriteToClose();
                dialogProgressBar_2.closeProgressBar();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                if (parkingInfoModel.getStatus() == 0) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(parkingInfoModel.getLat(), parkingInfoModel.getLng()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));
                } else {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(parkingInfoModel.getLat(), parkingInfoModel.getLng()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                if (polyline != null)
                    polyline.remove();

                polyline = mMap.addPolyline(polylineOptions);
                polyline.setWidth(8);
                polyline.setColor(Color.BLUE);
            }
        }
    }
}
