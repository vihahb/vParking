package com.xtel.vparking.view.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.dialog.DialogProgressBar;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.PlaceModel;
import com.xtel.vparking.presenter.AddParkingPresenter;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.inf.AddParkingView;
import com.xtel.vparking.view.adapter.AddParkingAdapter;
import com.xtel.vparking.view.widget.BitmapTransform;

import java.util.ArrayList;
import java.util.Calendar;

import gun0912.tedbottompicker.TedBottomPicker;

public class AddParkingActivity extends BasicActivity implements View.OnClickListener, AddParkingView {

    private AddParkingPresenter presenter;
    //    private ImageView img_picture;
    private TextView txt_image_number, txt_money;
    private EditText edt_parking_name, edt_parking_desc, edt_place_number, edt_address, edt_begin_time, edt_end_time;
    private RadioButton chk_tatca, chk_oto, chk_xemay;
    private SeekBar seek_money;

    private ViewPager viewPager;
    private ImageView img_load;

    private PlaceModel placeModel;
    private ArrayList<String> arrayList_file;
    private DialogProgressBar dialogProgressBar;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);

        initToolbar(R.id.toolbar_tao_bai_do, null);
        initWidger();
        initListener();

        initViewPager();
        initSelectMoney();

        presenter = new AddParkingPresenter(this);
    }

    private void initWidger() {
        txt_image_number = (TextView) findViewById(R.id.txt_tao_bai_do_image_number);
        img_load = (ImageView) findViewById(R.id.img_tao_bai_do_picture);
        txt_money = (TextView) findViewById(R.id.txt_tao_bai_do_money);

        chk_tatca = (RadioButton) findViewById(R.id.chk_tao_bai_do_tatca);
        chk_oto = (RadioButton) findViewById(R.id.chk_tao_bai_do_oto);
        chk_xemay = (RadioButton) findViewById(R.id.chk_tao_bai_do_xemay);

        edt_parking_name = (EditText) findViewById(R.id.edt_tao_bai_do_name);
        edt_parking_desc = (EditText) findViewById(R.id.edt_tao_bai_do_desc);
        edt_place_number = (EditText) findViewById(R.id.edt_tao_bai_do_empty);
        edt_address = (EditText) findViewById(R.id.edt_tao_bai_do_diacho);
        edt_begin_time = (EditText) findViewById(R.id.edt_tao_bai_do_begin_time);
        edt_end_time = (EditText) findViewById(R.id.edt_tao_bai_do_end_time);

        seek_money = (SeekBar) findViewById(R.id.seek_bar_tao_bai_do_money);
        viewPager = (ViewPager) findViewById(R.id.viewpager_tao_bai_do);
    }

    private void initListener() {
        edt_address.setOnClickListener(this);
        edt_begin_time.setOnClickListener(this);
        edt_end_time.setOnClickListener(this);
    }

    private void initViewPager() {
        arrayList_file = new ArrayList<>();
        AddParkingAdapter addParkingAdapter = new AddParkingAdapter(getSupportFragmentManager(), arrayList_file);
        viewPager.setAdapter(addParkingAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (arrayList_file.size() > 0) {
                    String text = (position + 1) + "/" + arrayList_file.size();
                    txt_image_number.setText(text);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initSelectMoney() {
        seek_money.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String money = (progress * 5) + "K";
                txt_money.setText(money);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void TakePicture(final View view) {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(AddParkingActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
                        Log.e("tb_uri", "uri: " + uri);
                        Log.e("tb_path", "uri.geta: " + uri.getPath());

                        showProgressBar(false, false, null, "Upda file...");

                        Picasso.with(AddParkingActivity.this)
                                .load(uri)
                                .transform(new BitmapTransform(1200, 1200))
                                .fit()
                                .centerCrop()
                                .into(img_load, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Bitmap bitmap = ((BitmapDrawable) img_load.getDrawable()).getBitmap();
                                        Log.e("tb_img_width", "how: " + bitmap.getWidth());
                                        presenter.postImage(bitmap);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                })
                .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());

//        Task.TakeBigPicture(AddParkingActivity.this, getSupportFragmentManager(), true, new RequestWithStringListener() {
//            @Override
//            public void onSuccess(String url) {
//                arrayList_file.add(url);
//                viewPager.getAdapter().notifyDataSetChanged();
//
//                if (arrayList_file.size() == 1)
//                    txt_image_number.setText("1/1");
//                else {
//                    String text = (viewPager.getCurrentItem() + 1) + "/" + arrayList_file.size();
//                    txt_image_number.setText(text);
//                }
//
////                closeProgressBar();
//            }
//
//            @Override
//            public void onError() {
////                closeProgressBar();
//                showShortToast("Không thể chọn ảnh");
//            }
//        });
    }

    private void getAddress() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            e.printStackTrace();
        }
    }

    private void getBeginTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddParkingActivity.this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edt_begin_time.setText(getHour(selectedHour) + ":" + getMinute(selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time.
        mTimePicker.show();
    }

    private void getEndTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddParkingActivity.this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edt_end_time.setText(getHour(selectedHour) + ":" + getMinute(selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time.
        mTimePicker.show();
    }

    private String getHour(int hour) {
        if (hour < 10)
            return "0" + hour;
        else
            return String.valueOf(hour);
    }

    private String getMinute(int minute) {
        if (minute < 10)
            return "0" + minute;
        else
            return String.valueOf(minute);
    }

    public void addPlace(View view) {
        int place = getPlaceNumber();

        if (place > -1) {
            place = place + 1;
            edt_place_number.setText(String.valueOf(place));
        }
    }

    public void removePlace(View view) {
        int place = getPlaceNumber();

        if (place > 0) {
            place = place - 1;
            edt_place_number.setText(String.valueOf(place));
        }
    }

    private int getPlaceNumber() {
        try {
            if (edt_place_number.getText().toString().isEmpty())
                return 0;
            return Integer.valueOf(edt_place_number.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void addParking(View view) {
        if (checkInputData(view)) {
            addParkingNow(view);
        }
    }

    private boolean checkInputData(View view) {
        if (arrayList_file.size() == 0) {
            Snackbar.make(view, getString(R.string.loi_chonanh), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (placeModel == null) {
            Snackbar.make(view, getString(R.string.loi_vitri), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (edt_parking_name.getText().toString().isEmpty()) {
            Snackbar.make(view, getString(R.string.loi_nhapten), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (checkNumberInput(edt_place_number.getText().toString()) <= 0) {
            Snackbar.make(view, getString(R.string.loi_chotrong), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (seek_money.getProgress() == 0) {
            Snackbar.make(view, getString(R.string.loi_chontien), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int checkNumberInput(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int getParkingType() {
        if (chk_tatca.isChecked())
            return 6;
        if (chk_oto.isChecked())
            return 2;
        if (chk_xemay.isChecked())
            return 3;
        return 6;
    }

    private void addParkingNow(final View view) {
        showProgressBar(false, false, null, getString(R.string.adding));
        presenter.addParking(placeModel.getLatitude(), placeModel.getLongtitude(), getParkingType(), edt_address.getText().toString(),
                edt_begin_time.getText().toString(), edt_end_time.getText().toString(), edt_parking_name.getText().toString(),
                edt_parking_desc.getText().toString(), Integer.parseInt(edt_place_number.getText().toString()),
                seek_money.getProgress(), arrayList_file);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(8);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                if (placeModel == null)
                    placeModel = new PlaceModel();
                placeModel.setAddress(place.getAddress().toString());
                placeModel.setLatitude(place.getLatLng().latitude);
                placeModel.setLongtitude(place.getLatLng().longitude);

                edt_address.setText(place.getAddress());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.edt_tao_bai_do_diacho) {
            getAddress();
        } else if (id == R.id.edt_tao_bai_do_begin_time) {
            getBeginTime();
        } else if (id == R.id.edt_tao_bai_do_end_time) {
            getEndTime();
        }
    }

    @Override
    public void onTakePictureSucces(String url) {
        arrayList_file.add(url);
        viewPager.getAdapter().notifyDataSetChanged();

        if (arrayList_file.size() == 1)
            txt_image_number.setText("1/1");
        else {
            String text = (viewPager.getCurrentItem() + 1) + "/" + arrayList_file.size();
            txt_image_number.setText(text);
        }

//        if (arrayList_file.size() > 0) {
//            img_load.setVisibility(View.GONE);
//            img_load.setImageResource(R.mipmap.ic_parking_background);
//        } else {
//            img_load.setImageResource(View.VISIBLE);
            img_load.setImageResource(R.mipmap.ic_parking_background);
//        }

        closeProgressBar();
    }

    @Override
    public void onTakePictureError() {
        closeProgressBar();
    }

    @Override
    public void onAddParkingSuccess(final int id) {
        closeProgressBar();
        showDialog("THÔNG BÁO", "Tin đã được đăng thành công", "OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                try {
                    intent.putExtra(Constants.INTENT_PARKING_ID, id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                setResult(99, intent);
                finish();
            }
        });
    }

    @Override
    public void onAddParkingError(Error error) {
        closeProgressBar();
        showShortToast(JsonParse.getCodeMessage(error.getCode(), getString(R.string.loi_addparking)));
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}