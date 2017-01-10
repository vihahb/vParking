package com.xtel.vparking.view.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.ResponseHandle;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.ParkingModel;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Favotire;
import com.xtel.vparking.model.entity.RESP_Parking_Info;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.view.activity.HomeActivity;
import com.xtel.vparking.view.activity.inf.FavoriteView;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/5/2016
 */

public class FavoriteAdapter extends RecyclerSwipeAdapter<FavoriteAdapter.ViewHolder> {
    private ArrayList<Favotire> arrayList;
    private FavoriteView view;
    private ProgressDialog progressDialog;

    public FavoriteAdapter(ArrayList<Favotire> arrayList, FavoriteView view) {
        this.arrayList = arrayList;
        this.view = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Favotire favotire = arrayList.get(position);

        holder.txt_name.setText(favotire.getParking_name());
        holder.txt_address.setText(favotire.getAddress());
        holder.txt_money.setText((favotire.getPrice() + " K"));
        holder.txt_time.setText(Constants.getTime(favotire.getBegin(), favotire.getEnd()));

        if (favotire.getImage() != null || !favotire.getImage().isEmpty())
            Picasso.with(view.getActivity())
                    .load(favotire.getImage())
                    .placeholder(R.mipmap.ic_parking_background)
                    .error(R.mipmap.ic_parking_background)
                    .fit()
                    .centerCrop()
                    .into(holder.img_avatar);
        else
            holder.img_avatar.setImageResource(R.mipmap.ic_parking_background);

        holder.img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkInfo.isOnline(view.getActivity())) {
                    view.showShortToast(view.getActivity().getString(R.string.no_internet));
                    return;
                }

                HomeActivity.getView().onViewParkingSelected(favotire.getId());
            }
        });

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkInfo.isOnline(view.getActivity())) {
                    view.showShortToast(view.getActivity().getString(R.string.no_internet));
                    return;
                }

                mItemManger.closeItem(position);
                askDelete(position);
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

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_swipe_favorite;
    }

    private void showProgressBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(view.getActivity(), R.style.AppCompatAlertDialogStyle);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(view.getActivity().getString(R.string.doing));
            progressDialog.show();
        } else if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void closeProgressBar() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void askDelete(final int position) {
        final Dialog dialog = new Dialog(view.getActivity(), R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.findViewById(R.id.dialog_txt_title).setVisibility(View.GONE);
        TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_txt_message);
        Button btn_negative = (Button) dialog.findViewById(R.id.dialog_btn_negative);
        Button btn_positive = (Button) dialog.findViewById(R.id.dialog_btn_positive);

        txt_message.setText("Xóa bãi đỗ khỏi danh sách yêu thích?");
        btn_negative.setText("Hủy bỏ");
        btn_positive.setText("Đồng ý");

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showProgressBar();
                deleteFavorite(position);
            }
        });

        dialog.show();
    }

    private void deleteFavorite(final int position) {
        ParkingModel.getInstanse().addToFavorite(arrayList.get(position).getId(), new ResponseHandle<RESP_Parking_Info>(RESP_Parking_Info.class) {
            @Override
            public void onSuccess(RESP_Parking_Info obj) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressBar();
                        arrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        view.onRemoveItemSuccess();
                    }
                }, 500);
            }

            @Override
            public void onError(Error error) {
                if (error.getCode() == 2)
                    getNewSessionAddToFavorite(position);
                else
                    JsonParse.getCodeError(view.getActivity(), null, error.getCode(), "Không thể xóa khỏi danh sách yêu thích");
            }
        });
    }

    private void getNewSessionAddToFavorite(final int position) {
        GetNewSession.getNewSession(view.getActivity(), new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                deleteFavorite(position);
            }

            @Override
            public void onError() {
                closeProgressBar();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private LinearLayout layout_content;
        private ImageButton img_view, img_delete;
        private TextView txt_name, txt_time, txt_address, txt_money;
        private ImageView img_avatar;

        ViewHolder(View itemView) {
            super(itemView);
            img_avatar = (ImageView) itemView.findViewById(R.id.item_layout_favorite_image);
            layout_content = (LinearLayout) itemView.findViewById(R.id.item_layout_favorite_content);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.item_swipe_favorite);
            img_view = (ImageButton) itemView.findViewById(R.id.item_img_favorite_view);
            img_delete = (ImageButton) itemView.findViewById(R.id.item_img_favorite_delete);
            txt_name = (TextView) itemView.findViewById(R.id.item_txt_favorite_name);
            txt_time = (TextView) itemView.findViewById(R.id.item_txt_favorite_time);
            txt_address = (TextView) itemView.findViewById(R.id.item_txt_favorite_address);
            txt_money = (TextView) itemView.findViewById(R.id.item_txt_favorite_money);
        }
    }
}