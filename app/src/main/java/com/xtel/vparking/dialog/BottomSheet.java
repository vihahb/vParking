package com.xtel.vparking.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.DialogListener;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Prices;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.adapter.VerhicleInParkingAdapter;
import com.xtel.vparking.view.adapter.ViewImageStoreAdapter;
import com.xtel.vparking.view.widget.WidgetHelper;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 11/24/2016
 */

public class BottomSheet {
    private Activity activity;
    private RESP_Parking_Info resp_parking_info;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private ImageView img_favorite;
    private TextView txt_address, txt_user_name, txt_user_phone, txt_time, txt_parking_name, txt_dat_cho, txt_picture_count;
    private ImageView img_verhicle_car, img_verhicle_moto, img_verhicle_bike;
    private RatingBar ratingBar;
    private Button btn_danduong;
    private LinearLayout view_header, view_content;
    private ArrayList<String> arrayList_bottom_sheet;
    private boolean addingToFavorite;

    private VerhicleInParkingAdapter verhicleInParkingAdapter;
    private ArrayList<Prices> list_price;

    private ImageButton img_header_favorite, img_header_close, img_show_qr;
    private TextView txt_header_name, txt_header_time, txt_header_address, txt_header_empty, txt_header_money;

    private int header_height;

    public BottomSheet(Activity activity, View view, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;

        initView(view);
        initRatingBar(view);
        initListener();
        initRecycerView(view);
        initViewPager(view);
    }

    private void initView(View view) {
        view_header = (LinearLayout) view.findViewById(R.id.layout_dialog_bottom_sheet_header);
        view_content = (LinearLayout) view.findViewById(R.id.layout_dialog_bottom_sheet_content);
        img_header_favorite = (ImageButton) view.findViewById(R.id.img_dialog_bottom_sheet_header_favorite);
        img_header_close = (ImageButton) view.findViewById(R.id.img_dialog_bottom_sheet_header_close);
        img_show_qr = (ImageButton) view.findViewById(R.id.img_dialog_bottom_sheet_qr);
        txt_header_name = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_name);
        txt_header_time = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_time);
        txt_header_address = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_address);
        txt_header_empty = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_total_place);
        txt_header_money = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_total_money);

        img_favorite = (ImageView) view.findViewById(R.id.img_dialog_bottom_sheet_favorite);
        txt_picture_count = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_picture_count);
        txt_address = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_address);
        txt_user_name = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_user_name);
        txt_user_phone = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_phone);
        txt_time = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_time);
        txt_parking_name = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_parking_name);
        txt_dat_cho = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_datcho);

        img_verhicle_car = (ImageView) view.findViewById(R.id.img_dialog_bottom_sheet_parking_car);
        img_verhicle_moto = (ImageView) view.findViewById(R.id.img_dialog_bottom_sheet_parking_moto);
        img_verhicle_bike = (ImageView) view.findViewById(R.id.img_dialog_bottom_sheet_parking_bike);

        btn_danduong = (Button) view.findViewById(R.id.btn_dialog_bottom_sheet_chiduong);
    }

    private void initRatingBar(View view) {
        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar_dialog_bottom_sheet);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.parseColor("#f7941e"), PorterDuff.Mode.SRC_ATOP);
        ratingBar.setEnabled(false);
    }

    private void initListener() {
        img_header_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addingToFavorite)
                    new AddToFavorite(v).execute(resp_parking_info.getId());
            }
        });

        img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addingToFavorite)
                    new AddToFavorite(v).execute(resp_parking_info.getId());
            }
        });
    }

    private void initRecycerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_dialog_bottom_sheet);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        list_price = new ArrayList<>();
        verhicleInParkingAdapter = new VerhicleInParkingAdapter(activity, list_price);
        recyclerView.setAdapter(verhicleInParkingAdapter);
    }

    private void initViewPager(View view) {
        arrayList_bottom_sheet = new ArrayList<>();
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_dialog_bottom_sheet);
        ViewImageStoreAdapter parkingDetailAdapter = new ViewImageStoreAdapter(fragmentManager, arrayList_bottom_sheet);
        viewPager.setAdapter(parkingDetailAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                txt_picture_count.setText((position + 1) + "/" + arrayList_bottom_sheet.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initData(RESP_Parking_Info resp_parking_info) {
        this.resp_parking_info = resp_parking_info;
        Log.e("result", JsonHelper.toJson(resp_parking_info));

        setUpHeader();
        setUpContent();
        setUpViewpager();
        setUpRecyclerView();
        setUpFavorite();
        setUpShowVerhicle();
    }

    private void setUpHeader() {
        String total_place = Constants.getPlaceNumberNoText(resp_parking_info.getEmpty_number());
        txt_header_name.setText(resp_parking_info.getParking_name());
        txt_header_time.setText(Constants.getTime(resp_parking_info.getBegin_time(), resp_parking_info.getEnd_time()));
        txt_header_address.setText(resp_parking_info.getAddress());
        txt_header_empty.setText(total_place);
        txt_header_money.setText((resp_parking_info.getPrices().get(0).getPrice() + " K"));
        header_height = view_header.getHeight();

        if (total_place.equals(activity.getString(R.string.limited)))
            txt_header_empty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_not_empty, 0, 0, 0);
        else
            txt_header_empty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_still_empty, 0, 0, 0);
    }

    private void setUpContent() {
        txt_address.setText(resp_parking_info.getAddress());
        txt_user_name.setText(Constants.getUserName(resp_parking_info.getParking_owner().getFullname()));
        txt_user_phone.setText(Constants.getUserPhone(resp_parking_info.getParking_owner().getPhone()));
        txt_time.setText(Constants.getTime(resp_parking_info.getBegin_time(), resp_parking_info.getEnd_time()));
        txt_parking_name.setText(resp_parking_info.getParking_name());
        txt_dat_cho.setText(resp_parking_info.getEmpty_number());

        txt_address.setSelected(true);
        txt_user_name.setSelected(true);
        txt_user_phone.setSelected(true);
        txt_time.setSelected(true);
        txt_parking_name.setSelected(true);
    }

    private void setUpViewpager() {
        arrayList_bottom_sheet.clear();

        if (resp_parking_info.getPictures().size() > 0)
            for (int i = 0; i < resp_parking_info.getPictures().size(); i++) {
                arrayList_bottom_sheet.add(resp_parking_info.getPictures().get(i).getUrl());
            }
        else
            arrayList_bottom_sheet.add(null);

        String picture_count = "1/" + arrayList_bottom_sheet.size();
        txt_picture_count.setText(picture_count);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private void setUpRecyclerView() {
        list_price.addAll(resp_parking_info.getPrices());
        verhicleInParkingAdapter.notifyDataSetChanged();
    }

    private void setUpFavorite() {
        if (this.resp_parking_info.getFavorite() == 1) {
            img_header_favorite.setImageResource(R.drawable.ic_action_favorite_selected);
            img_favorite.setImageResource(R.drawable.ic_action_favorite_selected);
        } else {
            img_header_favorite.setImageResource(R.drawable.ic_action_favorite_normal);
            img_favorite.setImageResource(R.drawable.ic_action_favorite_normal);
        }
    }

    private void setUpShowVerhicle() {
        for (int i = list_price.size() - 1; i >= 0; i--) {
            showVerhicle(list_price.get(i).getPrice_for());
        }
    }

    private void showVerhicle(int type) {
        switch (type) {
            case 1:
                img_verhicle_bike.setImageResource(R.mipmap.ic_bike_black_48);
            case 2:
                img_verhicle_moto.setImageResource(R.mipmap.ic_moto_black_48);
            case 3:
                img_verhicle_car.setImageResource(R.mipmap.ic_car_black_48);
        }
    }

    public void setMarginHeader(float view) {
        int _view = header_height - ((int) (header_height * view));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view_content.getLayoutParams();
        params.setMargins(0, _view, 0, 0);
        view_content.setLayoutParams(params);
    }

    public void clearData() {
        img_header_close.setVisibility(View.GONE);
        img_header_favorite.setVisibility(View.VISIBLE);

        list_price.clear();
        verhicleInParkingAdapter.notifyDataSetChanged();

        img_verhicle_bike.setImageResource(R.mipmap.ic_bike_gray);
        img_verhicle_moto.setImageResource(R.mipmap.ic_moto_gray);
        img_verhicle_car.setImageResource(R.mipmap.ic_car_gray);

        arrayList_bottom_sheet.clear();
        try {
            viewPager.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onContentCliecked(View.OnClickListener onClickListener) {
        view_header.setOnClickListener(onClickListener);
    }

    public void onGuidClicked(final DialogListener dialogListener) {
        btn_danduong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onClicked(resp_parking_info);
            }
        });
    }

    public void onShowQrClicked(View.OnClickListener onClickListener) {
        img_show_qr.setOnClickListener(onClickListener);
    }

    public void onCloseClicked(View.OnClickListener onClickListener) {
        img_header_close.setOnClickListener(onClickListener);
    }

    public void changeFavoriteToClose() {
        img_header_close.setVisibility(View.VISIBLE);
        img_header_favorite.setVisibility(View.GONE);
    }

    private DialogProgressBar dialogProgressBar;

    private class AddToFavorite extends AsyncTask<Integer, Void, String> {
        private View view;
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private int id;

        AddToFavorite(View view) {
            addingToFavorite = true;
            if (dialogProgressBar == null)
                dialogProgressBar = new DialogProgressBar(activity, false, false, null, activity.getString(R.string.doing));
            if (!dialogProgressBar.isShowing())
                dialogProgressBar.showProgressBar();
            this.view = view;
        }

        @Override
        protected String doInBackground(Integer... params) {
            this.id = params[0];
            try {
                OkHttpClient client = new OkHttpClient();

                String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
                String url = Constants.SERVER_PARKING + Constants.PARKING_FAVORITE;

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(Constants.JSON_PARKING_ID, params[0]);

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header(Constants.JSON_SESSION, session)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null || s.isEmpty()) {
                if (resp_parking_info.getFavorite() == 1) {
                    resp_parking_info.setFavorite(0);
                    Toast.makeText(activity, "Đã xóa bãi đỗ khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    resp_parking_info.setFavorite(1);
                    Toast.makeText(activity, "Đã thêm bãi đỗ vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                }

                setUpFavorite();
                addingToFavorite = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogProgressBar.closeProgressBar();
                    }
                }, 500);
            } else {
                Error error = JsonHelper.getObjectNoException(s, Error.class);

                if (error != null)
                    if (error.getCode() == 2)
                        getNewSessionAddToFavorite(view, id);
                    else {
                        addingToFavorite = false;
                        dialogProgressBar.closeProgressBar();
                        JsonParse.getCodeError(activity, view, error.getCode(), "Không thể thêm vào danh sách yêu thích");
                    }
            }
        }
    }

    private void getNewSessionAddToFavorite(final View view, final int id) {
        GetNewSession.getNewSession(activity, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                new AddToFavorite(view).execute(id);
            }

            @Override
            public void onError() {
                dialogProgressBar.closeProgressBar();
                Toast.makeText(activity, "Không thể thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }
}