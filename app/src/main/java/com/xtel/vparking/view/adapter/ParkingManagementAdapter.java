package com.xtel.vparking.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.ParkingInfo;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 11/5/2016.
 */

public class ParkingManagementAdapter extends RecyclerView.Adapter<ParkingManagementAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ParkingInfo> arrayList;

    public ParkingManagementAdapter(Context context, ArrayList<ParkingInfo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quan_ly_bai_do, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParkingInfo parkingInfo = arrayList.get(position);

        if (parkingInfo.getPictures() != null && parkingInfo.getPictures().size() > 0) {
            String picture = parkingInfo.getPictures().get(0).getUrl();

            if (picture != null && !picture.isEmpty())
                Picasso.with(context)
                        .load(picture)
                        .error(R.mipmap.ic_parking_background)
                        .fit()
                        .into(holder.img_avatar);
            else
                holder.img_avatar.setImageResource(R.mipmap.ic_parking_background);
        } else
            holder.img_avatar.setImageResource(R.mipmap.ic_parking_background);

        Log.e("adapter_number_place", "null k: " + parkingInfo.getEmpty_number());

        holder.txt_name.setText(parkingInfo.getParking_name());
        holder.txt_address.setText(parkingInfo.getAddress());
        holder.txt_number.setText(Constants.getPlaceNumber(context, parkingInfo.getEmpty_number()));
        setStatus(holder.txt_empty, parkingInfo.getStatus());
    }

    private void setStatus(TextView textView, double status) {
        if (status == 0) {
            textView.setText(context.getString(R.string.controng));
            textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.ic_still_empty), null, null, null);
        } else {
            textView.setText(context.getString(R.string.hetcho));
            textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.ic_still_empty), null, null, null);
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_avatar;
        private ImageButton img_minus, img_plus;
        private TextView txt_name, txt_address, txt_empty, txt_number;

        public ViewHolder(View itemView) {
            super(itemView);

            img_avatar = (ImageView) itemView.findViewById(R.id.img_item_quan_ly_bai_do_avatar);
            img_minus = (ImageButton) itemView.findViewById(R.id.img_quan_ly_bai_do_minus);
            img_plus = (ImageButton) itemView.findViewById(R.id.img_quan_ly_bai_do_plus);
            txt_name = (TextView) itemView.findViewById(R.id.txt_item_quan_ly_bai_do_name);
            txt_address = (TextView) itemView.findViewById(R.id.txt_item_quan_ly_bai_do_address);
            txt_empty = (TextView) itemView.findViewById(R.id.txt_item_quan_ly_bai_do_empty);
            txt_number = (TextView) itemView.findViewById(R.id.txt_item_quan_ly_bai_do_number);
        }
    }

    public void addNewItem(ParkingInfo parkingInfo) {
        arrayList.add(parkingInfo);
        notifyItemRangeInserted(arrayList.size() - 1, arrayList.size());
//        notifyItemInserted(arrayList.size() - 1);
    }
}
