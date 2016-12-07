package com.xtel.vparking.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.dialog.BottomSheet;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Find;
import com.xtel.vparking.model.entity.MarkerModel;
import com.xtel.vparking.model.entity.Parking;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.presenter.HomeFragmentPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.FindAdvancedActivity;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.inf.HomeFragmentView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 11/15/2013.
 */

public class HomeFragment extends BasicFragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, LocationListener, View.OnClickListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener, HomeFragmentView {

    private HomeFragmentPresenter presenter;

    private final String TAG = "HomeFragment";
    private GoogleMap mMap, mMap_bottom;
    public static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ArrayList<MarkerModel> markerList;
    private FloatingActionButton fab_thongbao, fab_location;

    public BottomSheetBehavior bottomSheetBehavior;
    private boolean isFindMyLocation, isScrollDown, isCanLoadMap = true;
    private int isLoadNewParking = 0;

    private Marker pickMarker;
    private Polyline polyline;

    private BottomSheet dialogBottomSheet;
    private RESP_Parking_Info resp_parking_info;

    private int actionType = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createLocationRequest();
        initGoogleMap();
        initWidget(view);
        initSearchView();
        initBottomSheet(view);
        initGooogleBottomSheet();
        initBottomSheetView(view);
        presenter = new HomeFragmentPresenter(this);
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
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(20.725517, 104.634451), new LatLng(21.937487, 106.759183)));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 15));
                    if (!isFindMyLocation)
                        isFindMyLocation = true;
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
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
        final NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.bottom_sheet_home);

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

                    dialogBottomSheet.changeCloseToFavorite();
                    dialogBottomSheet.clearData();
                    dialogBottomSheet.showHeader();
                    nestedScrollView.scrollTo(0, 0);
                    mMap_bottom.clear();
                    resp_parking_info = null;
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
                actionType = 2;
                presenter.getMyLocation();
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
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        double latitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude;
        double longtitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude;
        presenter.getParkingAround(latitude, longtitude, -1, -1, null, null);
    }

    private void showDialogParkingDetail() {
        hideFloatingActionButton(fab_location);
        hideFloatingActionButton(fab_thongbao);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        dialogBottomSheet.initData(resp_parking_info);

        if (actionType == 3)
            dialogBottomSheet.changeFavoriteToClose();

        if (resp_parking_info.getStatus() == 0) {
            mMap_bottom.addMarker(new MarkerOptions()
                    .position(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));
        } else {
            mMap_bottom.addMarker(new MarkerOptions()
                    .position(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));
        }
        mMap_bottom.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()), 15));
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
        mMap.setOnCameraIdleListener(this);

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

        if (!isFindMyLocation)
            isFindMyLocation = true;
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
                            showProgressBar(false, false, null, getString(R.string.parking_get_data));
                            presenter.getParkingInfo(markerList.get(finalI).getId());
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
            isCanLoadMap = false;

            if (isLoadNewParking == 0) {
                isLoadNewParking++;
            } else if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            double latitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude;
            double longtitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude;
            presenter.getParkingAround(latitude, longtitude, -1, -1, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.layout_parking_timkiem) {
            startActivityForResult(new Intent(getContext(), FindAdvancedActivity.class), Constants.FIND_ADVANDCED_RQ);
        } else if (id == R.id.fab_parking_location) {
            if (isCanLoadMap) {
                if (pickMarker != null)
                    pickMarker.remove();

                actionType = 1;
                presenter.getMyLocation();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!isFindMyLocation)
            if (location != null) {

                isFindMyLocation = true;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!isFindMyLocation) {
            actionType = 0;
            presenter.getMyLocation();
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
        presenter.checkResultSearch();

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
                        Find findModel = (Find) data.getExtras().getSerializable(Constants.FIND_MODEL);

                        if (findModel != null && isCanLoadMap) {

                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));

                            if (!isFindMyLocation)
                                isFindMyLocation = true;
                            isCanLoadMap = false;

                            presenter.getParkingAround(mLastLocation.getLatitude(), mLastLocation.getLongitude(), findModel.getMoney(), findModel.getType(), null, null);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.error_find_advanced), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else
                mGoogleApiClient.connect();
        } else if (requestCode == HomeActivity.REQUEST_CODE && resultCode == HomeActivity.RESULT_GUID) {
            if (data != null) {
                int id = data.getIntExtra(Constants.ID_PARKING, -1);
                if (id != -1)
                    presenter.getParkingInfo(id);

                Log.e(this.getClass().getSimpleName(), "parking id: " + id);
            }
        }
    }

    private void clearMarker(final int possition) {
        if (possition == -1) {
            isCanLoadMap = true;
            return;
        }

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (possition < markerList.size()) {
                        Log.e("hf_position", "total: " + markerList.size());
                        for (int i = possition; i >= 0; i--) {
                            if (markerList.get(i) != null) {
                                markerList.get(i).getMarker().remove();
                                markerList.remove(i);
                            }
                        }
                        Log.e("pk_clear_marker", "done: " + markerList.size());
                    }
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isCanLoadMap = true;
    }

    @Override
    public void showShortToast(String message) {
        super.showShortToast(message);
        closeProgressBar();
    }

    @Override
    public void showLongToast(String message) {
        super.showLongToast(message);
    }

    @Override
    public void showProgressBar(boolean isTouchOutside, boolean isCancel, String title, String message) {
        super.showProgressBar(isTouchOutside, isCancel, title, message);
    }

    @Override
    public void closeProgressBar() {
        super.closeProgressBar();
    }

    @Override
    public void onSearchParking(int id) {
        if (markerList != null)
            clearMarker(markerList.size() - 1);
        if (!isFindMyLocation)
            isFindMyLocation = true;

        actionType = 3;
        isCanLoadMap = false;
        showProgressBar(false, false, null, getString(R.string.parking_get_data));
    }

    @Override
    public void onGetMyLocationSuccess(LatLng latLng) {
        switch (actionType) {
            case 0:
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                break;
            case 1:
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                break;
            case 2:
                if (resp_parking_info != null) {
                    showProgressBar(false, false, null, getString(R.string.parking_get_data));
                    presenter.getPolyLine(latLng.latitude, latLng.longitude, resp_parking_info.getLat(), resp_parking_info.getLng());
                }
                break;
            default:
                break;
        }
        if (!isFindMyLocation)
            isFindMyLocation = true;
    }

    @Override
    public void onGetParkingInfoSuccess(RESP_Parking_Info resp_parking_info) {
        closeProgressBar();

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng())));

        if (resp_parking_info.getStatus() == 0) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));

            markerList.add(new MarkerModel(marker, resp_parking_info.getId(), resp_parking_info.getLat(), resp_parking_info.getLng()));
        } else {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));

            markerList.add(new MarkerModel(marker, resp_parking_info.getId(), resp_parking_info.getLat(), resp_parking_info.getLng()));
        }

        isLoadNewParking = 0;
        this.resp_parking_info = resp_parking_info;
        showDialogParkingDetail();
    }

    @Override
    public void onGetParkingInfoError(Error error) {
        closeProgressBar();
        showShortToast(JsonParse.getCodeMessage(error.getCode(), getString(R.string.error_get_parking)));
    }

    @Override
    public void onGetParkingAroundSuccess(ArrayList<Parking> arrayList) {
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
                clearMarker(markerList.size() - 1);
            }
        } else {
            clearMarker(markerList.size() - 1);
        }
    }

    @Override
    public void onGetParkingAroundError(Error error) {
        isCanLoadMap = true;
        showShortToast(JsonParse.getCodeMessage(error.getCode(), "Không thể tìm bãi đỗ"));
    }

    @Override
    public void onGetPolylineSuccess(LatLng latLng, PolylineOptions polylineOptions) {
        closeProgressBar();
        isCanLoadMap = false;

        mMap.clear();
        dialogBottomSheet.changeFavoriteToClose();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (resp_parking_info.getStatus() == 0) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_blue)));
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(resp_parking_info.getLat(), resp_parking_info.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red)));
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        if (polyline != null)
            polyline.remove();

        polyline = mMap.addPolyline(polylineOptions);
        polyline.setWidth(10);
        polyline.setColor(Color.BLUE);
    }

    @Override
    public void onGetPolylineError(String error) {
        showShortToast(error);
    }
}
