package com.xtel.vparking.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Verhicle;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class VerhicleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Verhicle> arrayList;
    private final int view_title = 1, view_item = 0;

    public VerhicleAdapter(ArrayList<Verhicle> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == view_title)
            return new ViewTitle(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verhicle_title, parent, false));
        else
            return new ViewItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verhicle_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Verhicle verhicle = arrayList.get(position);

        if (holder instanceof ViewTitle) {
            ViewTitle view = (ViewTitle) holder;
        } else {
            ViewItem view = (ViewItem) holder;

            view.txt_name.setText(verhicle.getName());
            view.txt_made_by.setText(verhicle.getBrandname().getMadeby());
            view.txt_plate_number.setText(verhicle.getPlate_number());

            if (verhicle.getFlag_default() == 1)
                view.txt_default.setVisibility(View.VISIBLE);
            else
                view.txt_default.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return view_item;
    }

    private class ViewTitle extends RecyclerView.ViewHolder {

        private ViewTitle(View itemView) {
            super(itemView);
        }
    }

    private class ViewItem extends RecyclerView.ViewHolder {
        private TextView txt_name, txt_plate_number, txt_made_by, txt_default;

        private ViewItem(View itemView) {
            super(itemView);

            txt_name = (TextView) itemView.findViewById(R.id.item_txt_verhicle_name);
            txt_plate_number = (TextView) itemView.findViewById(R.id.item_txt_verhicle_car_number_plate);
            txt_made_by = (TextView) itemView.findViewById(R.id.item_txt_verhicle_made_by);
            txt_default = (TextView) itemView.findViewById(R.id.item_txt_verhicle_default);
        }
    }
}
