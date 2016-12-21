package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.ParkingInfo;
import com.xtel.vparking.view.activity.AddParkingActivity;
import com.xtel.vparking.view.activity.ViewParkingActivity;
import com.xtel.vparking.view.fragment.ManagementFragment;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 11/5/2016.
 */

public class ManagementAdapter extends RecyclerSwipeAdapter<ManagementAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<ParkingInfo> arrayList;

    public ManagementAdapter(Activity activity, ArrayList<ParkingInfo> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ParkingInfo parkingInfo = arrayList.get(position);

        if (parkingInfo.getPictures() != null && parkingInfo.getPictures().size() > 0) {
            String picture = parkingInfo.getPictures().get(0).getUrl();

            if (picture != null && !picture.isEmpty())
                Picasso.with(activity)
                        .load(picture)
                        .error(R.mipmap.ic_parking_background)
                        .fit()
                        .centerCrop()
                        .into(holder.img_avatar);
            else
                holder.img_avatar.setImageResource(R.mipmap.ic_parking_background);
        } else
            holder.img_avatar.setImageResource(R.mipmap.ic_parking_background);

        holder.txt_name.setText(parkingInfo.getParking_name());
        holder.txt_address.setText(parkingInfo.getAddress());
        holder.txt_number.setText(Constants.getPlaceNumber(parkingInfo.getEmpty_number()));
        setStatus(holder.txt_empty, parkingInfo.getStatus());

        holder.img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ViewParkingActivity.class);
                intent.putExtra(Constants.ID_PARKING, parkingInfo.getId());
                activity.startActivityForResult(intent, ManagementFragment.REQUEST_UPDATE);
            }
        });

        holder.img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddParkingActivity.class);
                intent.putExtra(Constants.PARKING_MODEL, parkingInfo);
                activity.startActivityForResult(intent, ManagementFragment.REQUEST_UPDATE);
            }
        });

        holder.layout_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mItemManger.isOpen(position))
                    holder.swipeLayout.open();
                else
                    holder.swipeLayout.close();
            }
        });

        mItemManger.bindView(holder.itemView, position);
    }

    private void setStatus(TextView textView, double status) {
        if (status == 0) {
            textView.setText(activity.getString(R.string.controng));
            textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_still_empty, 0, 0, 0);
        } else {
            textView.setText(activity.getString(R.string.hetcho));
            textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_still_empty, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_swipe_favorite;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private LinearLayout layout_content;
        private ImageView img_avatar;
        private ImageButton img_view, img_update;
        private TextView txt_name, txt_address, txt_empty, txt_number;

        ViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.item_swipe_favorite);
            layout_content = (LinearLayout) itemView.findViewById(R.id.item_management_layout_content);
            img_avatar = (ImageView) itemView.findViewById(R.id.img_item_management_avatar);
            txt_name = (TextView) itemView.findViewById(R.id.txt_item_management_name);
            txt_address = (TextView) itemView.findViewById(R.id.txt_item_management_address);
            txt_empty = (TextView) itemView.findViewById(R.id.txt_item_management_empty);
            txt_number = (TextView) itemView.findViewById(R.id.txt_item_management_number);

            img_view = (ImageButton) itemView.findViewById(R.id.item_management_img_view);
            img_update = (ImageButton) itemView.findViewById(R.id.item_management_img_update);
        }
    }

    public void addNewItem(ParkingInfo parkingInfo) {
        arrayList.add(parkingInfo);
        notifyItemRangeInserted(arrayList.size() - 1, arrayList.size());
    }

    public void updateItem(ParkingInfo parkingInfo) {
        for (int i = (arrayList.size() - 1); i >= 0; i--) {
            if (arrayList.get(i).getId() == parkingInfo.getId()) {
                arrayList.set(i, parkingInfo);
                notifyItemChanged(i, getItemCount());
                return;
            }
        }
    }
}
