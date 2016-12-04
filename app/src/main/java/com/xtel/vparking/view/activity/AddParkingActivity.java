package com.xtel.vparking.view.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.dialog.DialogNotification;
import com.xtel.vparking.dialog.DialogProgressBar;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.PlaceModel;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.utils.Task;
import com.xtel.vparking.view.adapter.AddParkingAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class AddParkingActivity extends AppCompatActivity implements View.OnClickListener {
    //    private ImageView img_picture;
    private TextView txt_image_number, txt_money;
    private EditText edt_parking_name, edt_parking_desc, edt_place_number, edt_address, edt_begin_time, edt_end_time;
    private RadioButton chk_tatca, chk_oto, chk_xemay;
    private SeekBar seek_money;

    private ViewPager viewPager;
//    private ArrayList<Fragment> arrayList_fragment;

    private PlaceModel placeModel;
    private ArrayList<String> arrayList_file;
    private DialogProgressBar dialogProgressBar;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);

        arrayList_file = new ArrayList<>();

        initToolbar();
        initWidger();
        initViewPager();
        initSelectMoney();
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tao_bai_do);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initWidger() {
//        img_picture = (ImageView) findViewById(R.id.img_tao_bai_do_picture);

        txt_image_number = (TextView) findViewById(R.id.txt_tao_bai_do_image_number);
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

        edt_address.setOnClickListener(this);
        edt_begin_time.setOnClickListener(this);
        edt_end_time.setOnClickListener(this);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager_tao_bai_do);
//        arrayList_fragment = new ArrayList<>();
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

    public void TakePicture(View view) {
        Task.TakeBigPicture(AddParkingActivity.this, getSupportFragmentManager(), true, new RequestWithStringListener() {
            @Override
            public void onSuccess(String url) {
                arrayList_file.add(url);
                viewPager.getAdapter().notifyDataSetChanged();

                if (arrayList_file.size() == 1)
                    txt_image_number.setText("1/1");
                else {
                    String text = (viewPager.getCurrentItem() + 1) + "/" + arrayList_file.size();
                    txt_image_number.setText(text);
                }
            }

            @Override
            public void onError() {

            }
        });
//        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(AddParkingActivity.this)
//                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
//                    @Override
//                    public void onImageSelected(final Uri uri) {
//                        Log.d("tb_uri", "uri: " + uri);
//                        Log.d("tb_path", "uri.getPath(): " + uri.getPath());
//
//                        if (dialogProgressBar == null)
//                            dialogProgressBar = new DialogProgressBar(AddParkingActivity.this, false, false, null, "Uploading File...");
//                        else
//                            dialogProgressBar.updateProgressBar(null, "Uploading File...");
//                        if (!dialogProgressBar.isShowing())
//                            dialogProgressBar.showProgressBar();
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Bitmap bitmap = getBitmapSize(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
//
//                                    if (bitmap != null) {
//                                        final File file = saveImageFile(bitmap);
//
//                                        if (file != null) {
//                                            postImageToServer(file);
//                                        } else
//                                            dialogProgressBar.hideProgressBar();
//                                    } else
//                                        dialogProgressBar.hideProgressBar();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, 200);
//                    }
//                })
//                .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
//                .create();
//
//        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

//    private Bitmap getBitmapSize(Bitmap bitmap) {
//        try {
//            long width = bitmap.getWidth(), height = bitmap.getHeight();
//            Log.e("tb_bitmap_old", width + "        " + height);
//
//            if (width > 1500 || height > 1500) {
//                int new_width, new_height;
//                while (width > 1500 || height > 1500) {
//                    width = width / 2;
//                    height = height / 2;
//                    Log.e("tb_bitmap_run", width + "       " + height);
//                }
//                new_width = (int) width;
//                new_height = (int) height;
//
//                Log.e("tb_bitmap_new", new_width + "         " + new_height);
//                return Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
//            }
//
//            return bitmap;
//        } catch (Exception e) {
//            Log.e("tb_error_image", e.toString());
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @SuppressWarnings("ResultOfMethodCallIgnored")
//    private File saveImageFile(Bitmap bitmap) {
//        try {
//            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vParking";
//            File dir = new File(file_path);
//
//            if (!dir.exists())
//                dir.mkdirs();
//
//            File file = new File(dir, System.currentTimeMillis() + ".png");
//            FileOutputStream fOut = new FileOutputStream(file);
//
//            if (bitmap != null) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
//            }
//
//            fOut.flush();
//            fOut.close();
//
//            return file;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private void postImageToServer(final File file) {
//        Ion.with(AddParkingActivity.this)
//                .load(Constants.SERVER_UPLOAD)
//                .setMultipartFile("fileUpload", file)
//                .asString()
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                        dialogProgressBar.closeProgressBar();
//                        if (e != null) {
//                            Log.e("tb_up_error", e.toString());
//                            Toast.makeText(AddParkingActivity.this, getString(R.string.error_server_request), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e("tb_up_result", result);
//                            ErrorModel errorModel = JsonParse.checkError(result);
//
//                            if (errorModel != null) {
//                                JsonParse.getCodeError(AddParkingActivity.this, null, errorModel.getCode(), getString(R.string.loi_addparking));
//                            } else {
//                                arrayList_file.add(result);
//                                viewPager.getAdapter().notifyDataSetChanged();
//
//                                if (arrayList_file.size() == 1)
//                                    txt_image_number.setText("1/1");
//                                else {
//                                    String text = (viewPager.getCurrentItem() + 1) + "/" + arrayList_file.size();
//                                    txt_image_number.setText(text);
//                                }
//                            }
//
//                            try {
//                                boolean delete = file.delete();
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                    }
//                });
//    }

    private void getAddress() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
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
        if (dialogProgressBar == null)
            dialogProgressBar = new DialogProgressBar(AddParkingActivity.this, false, false, null, getString(R.string.adding));
        else
            dialogProgressBar.updateProgressBar(null, getString(R.string.adding));
        if (!dialogProgressBar.isShowing())
            dialogProgressBar.showProgressBar();

        String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);

        JsonObject json = new JsonObject();
        json.addProperty(Constants.JSON_LAT, placeModel.getLatitude());
        json.addProperty(Constants.JSON_LNG, placeModel.getLongtitude());
        json.addProperty(Constants.JSON_TYPE, getParkingType());
        json.addProperty(Constants.JSON_ADDRESS, edt_address.getText().toString());

        if (!edt_begin_time.getText().toString().isEmpty())
            json.addProperty(Constants.JSON_BEGIN_TIME, edt_begin_time.getText().toString());
        if (!edt_end_time.getText().toString().isEmpty())
            json.addProperty(Constants.JSON_END_TIME, edt_end_time.getText().toString());

        json.addProperty(Constants.JSON_PARKING_NAME, edt_parking_name.getText().toString());

        if (!edt_parking_desc.getText().toString().isEmpty())
            json.addProperty(Constants.JSON_PARKING_DESC, "null");

        if (!edt_place_number.getText().toString().isEmpty()) {
            json.addProperty(Constants.JSON_TOTAL_PLACE, Integer.valueOf(edt_place_number.getText().toString()));
            json.addProperty(Constants.JSON_EMPTY_NUMBER, Integer.valueOf(edt_place_number.getText().toString()));
        }


//        Add Prices
        JsonArray all_prices = new JsonArray();
        JsonObject price = new JsonObject();
        price.addProperty(Constants.JSON_NAME, "");
        price.addProperty(Constants.JSON_PRICE, (seek_money.getProgress() * 5));
        price.addProperty(Constants.JSON_PRICE_TYPE, 1);
        all_prices.add(price);
        json.add(Constants.JSON_PRICES, all_prices);

        JsonArray all_picture = new JsonArray();
        for (int i = 0; i < arrayList_file.size(); i++) {
            JsonObject picture = new JsonObject();
            picture.addProperty(Constants.JSON_URL, arrayList_file.get(i));
            all_picture.add(picture);
        }
        json.add(Constants.JSON_PICTURES, all_picture);

        Log.e("tb_session", session);
        Log.e("tb_url", Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING);
        Log.e("tb_json", json.toString());

        String url = Constants.SERVER_PARKING + Constants.PARKING_ADD_PARKING;
        Ion.with(AddParkingActivity.this)
                .load(url)
                .setHeader(Constants.JSON_SESSION, session)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {
                        if (e != null) {
                            Snackbar.make(view, getString(R.string.error_server_request), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Log.e("tb_result", result.toString());
                            Error errorModel = JsonParse.checkError(result.toString());
                            if (errorModel != null) {
                                if (errorModel.getCode() == 2) {
                                    getNewSesstionAddParking(view);
                                } else {
                                    dialogProgressBar.closeProgressBar();
                                    JsonParse.getCodeError(AddParkingActivity.this, view, errorModel.getCode(), getString(R.string.loi_addparking));
                                }
                            } else {
                                dialogProgressBar.closeProgressBar();
                                DialogNotification dialogNotification = new DialogNotification(AddParkingActivity.this);
                                dialogNotification.showDialog("THÔNG BÁO", "Tin đã được đăng thành công", "OK");
                                dialogNotification.setOnButtonClicked(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();

                                        try {
                                            intent.putExtra(Constants.INTENT_PARKING_ID, result.get(Constants.JSON_ID).getAsString());
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                        setResult(99, intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void getNewSesstionAddParking(final View view) {
        GetNewSession.getNewSession(AddParkingActivity.this, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                addParkingNow(view);
            }

            @Override
            public void onError() {
                dialogProgressBar.closeProgressBar();
                Toast.makeText(AddParkingActivity.this, getString(R.string.error_session_invalid), Toast.LENGTH_SHORT).show();
            }
        });
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
//        else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
//            List<GalleryMedia> galleryMedias = data.getParcelableArrayListExtra(GalleryActivity.RESULT_GALLERY_MEDIA_LIST);
//
//            for (int i = 0; i < galleryMedias.size(); i++) {
//                Log.e("pk_list", "uri: " + galleryMedias.get(i).mediaUri());
//            }
//        }
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
}