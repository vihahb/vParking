package com.xtel.vparking.view.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.ParkingCheckIn;
import com.xtel.vparking.view.activity.inf.IViewCheckIn;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class ViewCheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ParkingCheckIn> arrayList;
    private IViewCheckIn checkedView;
    private boolean isLoadMore = true;

    private static final int view_item = 1, view_progress = 2;

    public ViewCheckInAdapter(ArrayList<ParkingCheckIn> arrayList, IViewCheckIn checkedView) {
        this.arrayList = arrayList;
        this.checkedView = checkedView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == view_item)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_check_in, parent, false));
        else
            return new ViewProgress(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isLoadMore && position == arrayList.size())
            checkedView.onEndlessScroll();

        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
            final ParkingCheckIn checkIn = arrayList.get(position);

            if (checkIn.getCheckin_type() == 1) {
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_black_24dp, 0, 0, 0);
            } else if (checkIn.getCheckin_type() == 2) {
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_motobike, 0, 0, 0);
            } else {
                view.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_bike_black_24dp, 0, 0, 0);
            }

            view.txt_name.setText(checkIn.getUser().getFullname());
            view.txt_time.setText(Constants.convertDataTime(checkIn.getCheckin_time()));
            view.txt_brand_name.setText(checkIn.getVehicle().getBrandname().getName());
            view.txt_plate_number.setText(checkIn.getVehicle().getPlate_number());

            if (checkIn.getUser().getPhone() == null)
                view.txt_phone.setText(checkedView.getActivity().getString(R.string.not_update));
            else
                view.txt_phone.setText(checkIn.getUser().getPhone());

            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetWorkInfo.isOnline(checkedView.getActivity())) {
                        checkedView.showShortToast(checkedView.getActivity().getString(R.string.no_internet));
                        return;
                    }

                    checkedView.onItemClicked(checkIn);
                }
            });
        } else {
            ViewProgress view = (ViewProgress) holder;
            view.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5c5ca7"), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public int getItemCount() {
        if (isLoadMore && arrayList.size() > 0)
            return arrayList.size() + 1;
        else
            return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == arrayList.size())
            return view_progress;
        else
            return view_item;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_icon, txt_name, txt_time, txt_phone, txt_brand_name, txt_plate_number;

        ViewHolder(View itemView) {
            super(itemView);

            txt_icon = (TextView) itemView.findViewById(R.id.item_txt_view_check_in_icon);
            txt_time = (TextView) itemView.findViewById(R.id.item_txt_view_check_in_time);
            txt_name = (TextView) itemView.findViewById(R.id.item_txt_view_check_in_name);
            txt_phone = (TextView) itemView.findViewById(R.id.item_txt_view_check_in_phone);
            txt_brand_name = (TextView) itemView.findViewById(R.id.item_txt_view_check_in_brand_name);
            txt_plate_number = (TextView) itemView.findViewById(R.id.item_view_check_in_txt_number_plate);
        }
    }

    private class ViewProgress extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        ViewProgress(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.item_progress_bar);
        }
    }

    public void setLoadMore(boolean isLoad) {
        isLoadMore = isLoad;
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void removeItem(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, getItemCount());
    }
}