package com.xtel.vparking.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.CheckInVerhicle;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.activity.inf.CheckInView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class CheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int view_title = 1, view_item = 0, type_car = 1111, type_bike = 2222;
    private ArrayList<Verhicle> arrayList;
    private CheckInView checkInView;

    public CheckInAdapter(ArrayList<Verhicle> arrayList, CheckInView checkInView) {
        this.arrayList = arrayList;
        this.checkInView = checkInView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == view_title)
            return new ViewTitle(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verhicle_title, parent, false));
        else
            return new ViewItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Verhicle verhicle = arrayList.get(position);

        if (holder instanceof ViewTitle) {
            ViewTitle view = (ViewTitle) holder;

            if (verhicle.getType() == type_car)
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_car_black, 0, 0, 0);
            else if (verhicle.getType() == type_bike)
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_moto_black, 0, 0, 0);
            else
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bike_black, 0, 0, 0);

            view.txt_title.setText(verhicle.getName());
        } else {
            ViewItem view = (ViewItem) holder;

            if (verhicle.getFlag_default() == 1)
                view.img_default.setImageResource(R.drawable.ic_action_green_dot);
            else
                view.img_default.setImageResource(R.drawable.ic_action_gray_dot);

            view.txt_name.setText(verhicle.getName());
            view.txt_made_by.setText(verhicle.getBrandname().getName());
            view.txt_plate_number.setText(verhicle.getPlate_number());
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetWorkInfo.isOnline(checkInView.getActivity())) {
                        checkInView.showShortToast(checkInView.getActivity().getString(R.string.no_internet));
                        return;
                    }

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
        private ImageView img_icon;

        private ViewTitle(View itemView) {
            super(itemView);

            img_icon = (ImageView) itemView.findViewById(R.id.img_icon_vehicle);
//            txt_icon = (TextView) itemView.findViewById(R.id.item_txt_verhicle_icon);
            txt_title = (TextView) itemView.findViewById(R.id.item_txt_verhicle_title);
        }
    }

    private class ViewItem extends RecyclerView.ViewHolder {
        private TextView txt_name, txt_plate_number, txt_made_by;
        private ImageView img_default;

        private ViewItem(View itemView) {
            super(itemView);

            txt_name = (TextView) itemView.findViewById(R.id.item_txt_check_in_name);
            txt_plate_number = (TextView) itemView.findViewById(R.id.item_txt_check_in_car_number_plate);
            txt_made_by = (TextView) itemView.findViewById(R.id.item_txt_check_in_made_by);
            img_default = (ImageView) itemView.findViewById(R.id.item_img_check_in_default);
        }
    }
}