package com.xtel.vparking.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.xtel.vparking.view.adapter.AddParkingAdapter;

/**
 * Created by Lê Công Long Vũ on 11/24/2016.
 */

public class BottomSheet {
    private Context context;
    private RESP_Parking_Info resp_parking_info;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private ImageView img_favorite;
    private TextView txt_address, txt_user_name, txt_user_age, txt_time, txt_parking_name, txt_cho_trong, txt_money, txt_dat_cho, txt_picture_count;
    private RatingBar ratingBar;
    private Button btn_danduong;
    private View view_header;
    private ArrayList<String> arrayList_bottom_sheet;
    private boolean addingToFavorite;

    private ImageButton img_header_favorite, img_header_close;
    private TextView txt_header_name, txt_header_time, txt_header_address, txt_header_empty, txt_header_money;

    public BottomSheet(Context context, View view, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;

        initView(view);
        initViewPager(view);
    }

    private void initView(View view) {
        view_header = (View) view.findViewById(R.id.layout_dialog_bottom_sheet_header);
        img_header_favorite = (ImageButton) view.findViewById(R.id.img_dialog_bottom_sheet_header_favorite);
        img_header_close = (ImageButton) view.findViewById(R.id.img_dialog_bottom_sheet_header_close);
        txt_header_name = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_name);
        txt_header_time = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_time);
        txt_header_address = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_address);
        txt_header_empty = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_total_place);
        txt_header_money = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_header_total_money);

        img_favorite = (ImageView) view.findViewById(R.id.img_dialog_bottom_sheet_favorite);
        txt_picture_count = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_picture_count);
        txt_address = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_address);
        txt_user_name = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_user_name);
        txt_user_age = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_age);
        txt_time = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_time);
        txt_parking_name = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_parking_name);
        txt_cho_trong = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_chotrong);
        txt_money = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_money);
        txt_dat_cho = (TextView) view.findViewById(R.id.txt_dialog_bottom_sheet_datcho);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar_dialog_bottom_sheet);
        btn_danduong = (Button) view.findViewById(R.id.btn_dialog_bottom_sheet_chiduong);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.parseColor("#f7941e"), PorterDuff.Mode.SRC_ATOP);

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

    private void initViewPager(View view) {
        arrayList_bottom_sheet = new ArrayList<>();
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_dialog_bottom_sheet);
        AddParkingAdapter parkingDetailAdapter = new AddParkingAdapter(fragmentManager, arrayList_bottom_sheet);
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
        initHeader();

        arrayList_bottom_sheet.clear();

        if (resp_parking_info.getPictures().size() > 0)
            for (int i = 0; i < resp_parking_info.getPictures().size(); i++) {
                arrayList_bottom_sheet.add(resp_parking_info.getPictures().get(i).getUrl());
            }
        else
            arrayList_bottom_sheet.add(null);

        viewPager.getAdapter().notifyDataSetChanged();

        this.resp_parking_info = resp_parking_info;
        String picture_count = "1/" + arrayList_bottom_sheet.size();

        txt_picture_count.setText(picture_count);
        txt_address.setText(resp_parking_info.getAddress());
        txt_user_name.setText("No name");
        txt_user_age.setText("No age");
        txt_time.setText(Constants.getTime(resp_parking_info.getBegin_time(), resp_parking_info.getEnd_time()));
        txt_parking_name.setText(resp_parking_info.getParking_name());
        txt_cho_trong.setText(Constants.getPlaceNumber(context, resp_parking_info.getEmpty_number()));

        txt_money.setText((resp_parking_info.getPrices().get(0).getPrice() + " K"));
        txt_dat_cho.setText("12,000");

        if (this.resp_parking_info.getFavorite() == 1) {
            img_favorite.setImageResource(R.mipmap.ic_favorite_red);
        } else {
            img_favorite.setImageResource(R.mipmap.ic_favorite_gray);
        }
    }

    private void initHeader() {
        if (view_header.getVisibility() == View.GONE)
            view_header.setVisibility(View.VISIBLE);

        txt_header_name.setText(resp_parking_info.getParking_name());
        txt_header_time.setText(Constants.getTime(resp_parking_info.getBegin_time(), resp_parking_info.getEnd_time()));
        txt_header_address.setText(resp_parking_info.getAddress());
        txt_header_empty.setText(Constants.getPlaceNumberAndTotal(context, resp_parking_info.getEmpty_number(), resp_parking_info.getTotal_place()));
        txt_header_money.setText((resp_parking_info.getPrices().get(0).getPrice() + " K"));

        if (this.resp_parking_info.getFavorite() == 1) {
            img_header_favorite.setImageResource(R.mipmap.ic_favorite_red);
        } else {
            img_header_favorite.setImageResource(R.mipmap.ic_favorite_gray);
        }
    }

    public void hideHeader() {
        view_header.setVisibility(View.GONE);
    }

    public void showHeader() {
        view_header.setVisibility(View.VISIBLE);
    }

    public boolean isShowHeader() {
        return view_header.getVisibility() == View.VISIBLE;
    }

    public void clearData() {
        arrayList_bottom_sheet.clear();
        viewPager.getAdapter().notifyDataSetChanged();
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

    public void onCloseClicked(View.OnClickListener onClickListener) {
        img_header_close.setOnClickListener(onClickListener);
    }

    public void changeFavoriteToClose() {
        img_header_close.setVisibility(View.VISIBLE);
        img_header_favorite.setVisibility(View.GONE);
    }

    public void changeCloseToFavorite() {
        img_header_close.setVisibility(View.GONE);
        img_header_favorite.setVisibility(View.VISIBLE);
    }

    private DialogProgressBar dialogProgressBar;

    private class AddToFavorite extends AsyncTask<Integer, Void, String> {
        private View view;
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private int id;

        AddToFavorite(View view) {
            addingToFavorite = true;
            if (dialogProgressBar == null)
                dialogProgressBar = new DialogProgressBar(context, false, false, null, context.getString(R.string.doing));
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

                Log.e("pk_fa_url", url);
                Log.e("pk_fa_session", session);
                Log.e("pk_fa_json", jsonObject.toString());

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header(Constants.JSON_SESSION, session)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("pk_fa_loi_request", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("pk_fa_result", "null k: " + s);

            if (s == null || s.isEmpty()) {
                if (resp_parking_info.getFavorite() == 1) {
                    resp_parking_info.setFavorite(0);
                    img_favorite.setImageResource(R.mipmap.ic_favorite_gray);
                    img_header_favorite.setImageResource(R.mipmap.ic_favorite_gray);
                    Toast.makeText(context, "Đã xóa bãi đỗ khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    resp_parking_info.setFavorite(1);
                    img_favorite.setImageResource(R.mipmap.ic_favorite_red);
                    img_header_favorite.setImageResource(R.mipmap.ic_favorite_red);
                    Toast.makeText(context, "Đã thêm bãi đỗ vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                }

                addingToFavorite = false;
                dialogProgressBar.closeProgressBar();
            } else {
                Error error = JsonHelper.getObjectNoException(s, Error.class);

                if (error != null)
                    if (error.getCode() == 2)
                        getNewSessionAddToFavorite(view, id);
                    else {
                        addingToFavorite = false;
                        dialogProgressBar.closeProgressBar();
                        JsonParse.getCodeError(context, view, error.getCode(), "Không thể thêm vào danh sách yêu thích");
                    }
                else {
                    addingToFavorite = false;
                    dialogProgressBar.closeProgressBar();

                    if (resp_parking_info.getFavorite() == 1) {
                        resp_parking_info.setFavorite(0);
                        img_favorite.setImageResource(R.mipmap.ic_favorite_gray);
                        img_header_favorite.setImageResource(R.mipmap.ic_favorite_gray);
                        Toast.makeText(context, "Đã xóa bãi đỗ khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    } else {
                        resp_parking_info.setFavorite(1);
                        img_favorite.setImageResource(R.mipmap.ic_favorite_red);
                        img_header_favorite.setImageResource(R.mipmap.ic_favorite_red);
                        Toast.makeText(context, "Đã thêm bãi đỗ vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void getNewSessionAddToFavorite(final View view, final int id) {
        GetNewSession.getNewSession(context, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                new AddToFavorite(view).execute(id);
            }

            @Override
            public void onError() {
                dialogProgressBar.closeProgressBar();
                Toast.makeText(context, "Không thể thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }
}