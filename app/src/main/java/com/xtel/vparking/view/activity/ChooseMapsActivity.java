package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.PlaceModel;
import com.xtel.vparking.presenter.ChooseMapsPresenter;
import com.xtel.vparking.view.activity.inf.IChooseMapsView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Lê Công Long Vũ on 12/22/2016.
 */

public class ChooseMapsActivity extends BasicActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener, View.OnClickListener, IChooseMapsView {

    private GoogleMap mMap;
    private Marker marker;
    private PlaceModel placeModel;
    private ChooseMapsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        presenter = new ChooseMapsPresenter(this);

        presenter.getData();
        initToolbar(R.id.map_toolbar, null);
        initGoogle();
        initView();
        initSearchView();
        presenter.initGoogleApi();
    }

    private void initGoogle() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng;
        if (placeModel != null)
            latLng = new LatLng(placeModel.getLatitude(), placeModel.getLongtitude());
        else
            latLng = new LatLng(21.026529, 105.831361);
        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void initView() {
        Button btn_choose_location = (Button) findViewById(R.id.btn_map_choose_location);
        btn_choose_location.setOnClickListener(this);
    }

    private void initSearchView() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_map);
        autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(20.725517, 104.634451), new LatLng(21.937487, 106.759183)));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_map_choose_location) {
            if (placeModel != null) {
                Intent intent = new Intent();
                intent.putExtra(AddParkingActivity.MODEL_FIND, placeModel);
                setResult(AddParkingActivity.RESULT_LOCATION, intent);
                finish();
            } else {
                showShortToast("Vui lòng chọn địa chỉ");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();
        else if (id == R.id.nav_choose_map)
            presenter.getMyLocation();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraMove() {
        if (marker != null)
            marker.remove();
        placeModel = null;
    }

    @Override
    public void onCameraIdle() {
        if (marker != null)
            marker.remove();

        new LoadPlace().execute();
    }

    @Override
    public void onStart() {
        presenter.onStart();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onGetData(PlaceModel placeModel) {
        this.placeModel = placeModel;
    }

    @Override
    public void onGetMyLocation(LatLng latLng) {
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    private class LoadPlace extends AsyncTask<Void, Void, String> {
        double latitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude;
        double longtitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude;

        @Override
        protected String doInBackground(Void... params) {
            try {
                Geocoder geocoder = new Geocoder(ChooseMapsActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String knownName = addresses.get(0).getFeatureName();

                for (int i = 0; i < addresses.size(); i++) {
                    Log.e(this.getClass().getSimpleName(), "address: " + addresses.get(i).toString());
                }

                String place = "";
                if (address != null && knownName != null)
                    if (!address.contains(knownName))
                        place += knownName;

                if (address != null) {
                    if (place.isEmpty())
                        place += address;
                    else
                        place += ", " + address;
                }
                if (city != null)
                    place += ", " + city;
                if (country != null)
                    place += ", " + country;

                return place;
            } catch (Exception e) {
                debug("error: " + e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            double new_latitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude;
            double new_longtitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude;

            if (new_latitude == latitude && new_longtitude == longtitude) {
                if (aVoid != null) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longtitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red))
                            .title(aVoid));
                    marker.showInfoWindow();

                    placeModel = new PlaceModel();
                    placeModel.setAddress(aVoid);
                    placeModel.setLatitude(latitude);
                    placeModel.setLongtitude(longtitude);
                }
            }
        }
    }
}