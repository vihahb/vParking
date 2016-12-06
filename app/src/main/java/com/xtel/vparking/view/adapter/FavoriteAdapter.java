package com.xtel.vparking.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Favotire;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.ParkingInfoActivity;
import com.xtel.vparking.view.activity.inf.FavoriteView;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 12/5/2016.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<Favotire> arrayList;

    public FavoriteAdapter(Activity activity, ArrayList<Favotire> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Favotire favotire = arrayList.get(position);

        holder.txt_name.setText(favotire.getParking_name());
        holder.txt_address.setText(favotire.getAddress());
        holder.txt_money.setText((favotire.getPrice() + " K"));
        holder.txt_time.setText(Constants.getTime(favotire.getBegin(), favotire.getEnd()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.ID_PARKING, favotire.getId());
                activity.setResult(HomeActivity.RESULT_GUID, intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_name, txt_time, txt_address, txt_money;

        ViewHolder(View itemView) {
            super(itemView);

            txt_name = (TextView) itemView.findViewById(R.id.item_txt_favorite_name);
            txt_time = (TextView) itemView.findViewById(R.id.item_txt_favorite_time);
            txt_address = (TextView) itemView.findViewById(R.id.item_txt_favorite_address);
            txt_money = (TextView) itemView.findViewById(R.id.item_txt_favorite_money);
        }
    }
}
