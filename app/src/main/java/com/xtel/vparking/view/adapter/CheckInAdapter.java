package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.CheckInVerhicle;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.activity.inf.CheckInView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class CheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private ArrayList<Verhicle> arrayList;
    private CheckInView checkInView;
    private final int view_title = 1, view_item = 0, type_car = 1111, type_bike = 2222;

    public CheckInAdapter(Activity activity, ArrayList<Verhicle> arrayList, CheckInView checkInView) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.checkInView = checkInView;
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
        final Verhicle verhicle = arrayList.get(position);

        if (holder instanceof ViewTitle) {
            ViewTitle view = (ViewTitle) holder;

            if (verhicle.getType() == type_car)
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_black_24dp, 0, 0, 0);
            else if (verhicle.getType() == type_bike)
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_motobike, 0, 0, 0);
            else
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_bike_black_24dp, 0, 0, 0);

            view.txt_title.setText(verhicle.getName());
        } else {
            ViewItem view = (ViewItem) holder;

            view.txt_name.setText(verhicle.getName());
            view.txt_made_by.setText(verhicle.getBrandname().getName());
            view.txt_plate_number.setText(verhicle.getPlate_number());
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckInVerhicle checkInVerhicle = new CheckInVerhicle();
                    checkInVerhicle.setCheckin_type(verhicle.getType());
                    checkInVerhicle.setVerhicle_id(verhicle.getId());
                    checkInView.onItemClicked(checkInVerhicle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).getType() > 100)
            return view_title;
        else
            return view_item;
    }

    private class ViewTitle extends RecyclerView.ViewHolder {
        private TextView txt_icon, txt_title;

        private ViewTitle(View itemView) {
            super(itemView);

            txt_icon = (TextView) itemView.findViewById(R.id.item_txt_verhicle_icon);
            txt_title = (TextView) itemView.findViewById(R.id.item_txt_verhicle_title);
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