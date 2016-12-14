package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.CheckIn;
import com.xtel.vparking.view.activity.inf.VerhicleCheckedView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class CheckedAdapter extends RecyclerView.Adapter<CheckedAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<CheckIn> arrayList;
    private VerhicleCheckedView verhicleCheckedView;

    public CheckedAdapter(Activity activity, ArrayList<CheckIn> arrayList, VerhicleCheckedView verhicleCheckedView) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.verhicleCheckedView = verhicleCheckedView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CheckIn checkIn = arrayList.get(position);

        if (checkIn.getCheckin_type() == 1) {
            holder.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_black_24dp, 0, 0, 0);
        } else if (checkIn.getCheckin_type() == 2) {
            holder.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_motobike, 0, 0, 0);
        } else {
            holder.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_bike_black_24dp, 0, 0, 0);
        }

        holder.txt_name.setText(checkIn.getVehicle().getName());
        holder.txt_plate_number.setText(checkIn.getVehicle().getPlate_number());
        holder.txt_time.setText(checkIn.getCheckin_time());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verhicleCheckedView.onItemClicked(checkIn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_icon, txt_name, txt_plate_number, txt_time;

        ViewHolder(View itemView) {
            super(itemView);

            txt_icon = (TextView) itemView.findViewById(R.id.item_txt_check_in_icon);
            txt_time = (TextView) itemView.findViewById(R.id.item_txt_check_in_time);
            txt_name = (TextView) itemView.findViewById(R.id.item_txt_check_in_name);
            txt_plate_number = (TextView) itemView.findViewById(R.id.item_txt_check_in_car_number_plate);
        }
    }
}