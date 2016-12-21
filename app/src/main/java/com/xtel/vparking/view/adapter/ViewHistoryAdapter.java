package com.xtel.vparking.view.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.CheckInHisObj;
import com.xtel.vparking.view.activity.inf.IViewHistory;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/19/2016.
 */

public class ViewHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CheckInHisObj> arrayList;
    private IViewHistory iViewHistory;
    ViewHolder viewHolder;
    private boolean isLoaded = true;

    private static final int v_item = 1, v_progress = 2;

    public ViewHistoryAdapter(ArrayList<CheckInHisObj> arrayList, IViewHistory iViewHistory) {
        this.arrayList = arrayList;
        this.iViewHistory = iViewHistory;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == v_item)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_checkin, parent, false));
        else
            return new ViewProgress(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            viewHolder = (ViewHolder) holder;
            final CheckInHisObj hisObj = arrayList.get(position);

            if (hisObj.getCheckin_type() == 1) {
                viewHolder.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_car_black_24dp, 0, 0, 0);
            } else if (hisObj.getCheckin_type() == 2) {
                viewHolder.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_motobike, 0, 0, 0);
            } else {
                viewHolder.txt_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_bike_black_24dp, 0, 0, 0);
            }

            String name_phone = hisObj.getUser().getFullname() + " (" + hisObj.getUser().getPhone() + ")";

            viewHolder.txt_name.setText(name_phone);
            viewHolder.txt_time_begin.setText(hisObj.getCheckin_time());
            viewHolder.txt_plate_number.setText(hisObj.getVehicle().getPlate_number());

//            if (!hisObj.getUser().getPhone().equals("") || hisObj.getUser().getPhone() != null){
//                viewHolder.txt_phone.setText(hisObj.getUser().getPhone());
//            } else
//                viewHolder.txt_phone.setText("Chưa có số điện thoại");

            if (hisObj.getCheckout_time() != null || !hisObj.getCheckout_time().equals("")) {
                viewHolder.txt_time_end.setText(hisObj.getCheckout_time());
            } else
                viewHolder.txt_time_end.setText("Chưa Check Out");


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetWorkInfo.isOnline(iViewHistory.getActivity())) {
                        iViewHistory.showShortToast(iViewHistory.getActivity().getString(R.string.no_internet));
                        return;
                    }
                    iViewHistory.onItemClicked(hisObj);
                }
            });
            setSelected();
        } else {
            ViewProgress viewProgress = (ViewProgress) holder;
            viewProgress.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5c5ca7"), PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setSelected() {
        viewHolder.txt_name.setSelected(true);
        viewHolder.txt_time_begin.setSelected(true);
        viewHolder.txt_time_end.setSelected(true);
    }

    @Override
    public int getItemCount() {
        if (isLoaded && arrayList.size() > 0)
            return arrayList.size() + 1;
        else
            return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == arrayList.size())
            return v_progress;
        else
            return v_item;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_icon, txt_name, txt_plate_number, txt_time_begin, txt_time_end;

        ViewHolder(View itemView) {
            super(itemView);

            txt_icon = (TextView) itemView.findViewById(R.id.item_txt_icon);
            txt_time_begin = (TextView) itemView.findViewById(R.id.item_txt_time_begin);
            txt_time_end = (TextView) itemView.findViewById(R.id.item_txt_time_end);
            txt_name = (TextView) itemView.findViewById(R.id.item_txt_name);
            txt_plate_number = (TextView) itemView.findViewById(R.id.item_txt_plate_number);
        }
    }

    class ViewProgress extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        ViewProgress(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.item_progress_bar);
        }
    }

    public void setLoadMore(boolean isLoad) {
        isLoaded = isLoad;
    }

    public void remove(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, getItemCount());
    }

}
