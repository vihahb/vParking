package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Prices;

import java.util.ArrayList;

/**
 * Created by Vulcl on 2/13/2017
 */

public class VerhicleInParkingAdapter extends RecyclerView.Adapter<VerhicleInParkingAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<Prices> arrayList;
    private String[] price_type;

    public VerhicleInParkingAdapter(Activity context, ArrayList<Prices> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        price_type = context.getResources().getStringArray(R.array.add_price_type);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verhicle_in_parking, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e("Verhicle", "pos " + position);

        Prices prices = arrayList.get(position);

        String text_verhicle = getPriceFor(prices.getPrice_for());
        String text_price = prices.getPrice() + "K/" + getPriceType(prices.getPrice_type());

        holder.txt_verhicle.setText(text_verhicle);
        holder.txt_price.setText(text_price);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_verhicle, txt_price;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_verhicle = (TextView) itemView.findViewById(R.id.item_verhicle_in_parking_txt_verhicle);
            txt_price = (TextView) itemView.findViewById(R.id.item_verhicle_in_parking_txt_price);
        }
    }

    private String getPriceType(int type) {
        return price_type[(type - 1)];
    }

    private String getPriceFor(int type) {
        switch (type) {
            case 1:
                return context.getString(R.string.xedap);
            case 2:
                return context.getString(R.string.xemay);
            case 3:
                return context.getString(R.string.oto);
            default:
                return "";
        }
    }
}
