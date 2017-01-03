package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.NetWorkInfo;
import com.xtel.vparking.model.entity.Verhicle;
import com.xtel.vparking.view.activity.inf.VerhicleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Lê Công Long Vũ on 12/10/2016.
 */

public class VerhicleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private ArrayList<Verhicle> arrayList;
    private VerhicleView verhicleView;
    private final int view_title = 1, view_item = 0, type_car = 1111, type_bike = 2222;

    public VerhicleAdapter(Activity activity, ArrayList<Verhicle> arrayList, VerhicleView verhicleView) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.verhicleView = verhicleView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == view_title)
            return new ViewTitle(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verhicle_title, parent, false));
        else
            return new ViewItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verhicle_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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

            view.txt_name.setText(verhicle.getName());
            view.txt_made_by.setText(verhicle.getBrandname().getName());
            view.txt_plate_number.setText(verhicle.getPlate_number());

            if (verhicle.getFlag_default() == 1)
                view.img_default.setImageResource(R.drawable.ic_action_green_dot);
            else
                view.img_default.setImageResource(R.drawable.ic_action_gray_dot);


            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetWorkInfo.isOnline(verhicleView.getActivity())) {
                        verhicleView.showShortToast(verhicleView.getActivity().getString(R.string.no_internet));
                        return;
                    }

                    verhicleView.onItemClicked(position, verhicle);
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
        private TextView txt_name, txt_plate_number, txt_made_by;
        private ImageView img_default;

        private ViewItem(View itemView) {
            super(itemView);

            img_default = (ImageView) itemView.findViewById(R.id.item_img_verhicle_default);
            txt_name = (TextView) itemView.findViewById(R.id.item_txt_verhicle_name);
            txt_plate_number = (TextView) itemView.findViewById(R.id.item_txt_verhicle_car_number_plate);
            txt_made_by = (TextView) itemView.findViewById(R.id.item_txt_verhicle_made_by);
        }
    }

    private void insertItem(Verhicle verhicle) {
        if (verhicle.getFlag_default() == 1)
            clearDefault(verhicle);

        for (int i = (arrayList.size() - 1); i > 0; i--) {
            if (arrayList.get(i).getType() == verhicle.getType()) {
                int pos = i + 1;
                arrayList.add(pos, verhicle);
                notifyItemInserted(pos);
                notifyItemChanged(pos, getItemCount());

                return;
            }
        }

        if (arrayList.size() > 0) {
            if (verhicle.getType() == 1) {
                arrayList.add(0, new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
                arrayList.add(1, verhicle);
            } else {
                arrayList.add(new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
                arrayList.add(verhicle);
            }
        } else {
            if (verhicle.getType() == 1) {
                arrayList.add(new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
                arrayList.add(verhicle);
            } else {
                arrayList.add(new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
                arrayList.add(verhicle);
            }

            notifyItemChanged((arrayList.size() - 2), getItemCount());
        }
    }

    private void updateItem(int position, Verhicle verhicle) {
        if (verhicle.getType() == arrayList.get(position).getType()) {
            arrayList.set(position, verhicle);
            notifyItemChanged(position, getItemCount());

            if (verhicle.getFlag_default() == 1)
                clearDefault(verhicle);
        } else {
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position, getItemCount());

            for (int i = (arrayList.size() - 1); i >= 0; i--) {
                if (arrayList.get(i).getType() == verhicle.getType()) {
                    if (verhicle.getType() == 1 && arrayList.get((arrayList.size() - 1)).getType() > 1000) {
                        arrayList.remove((arrayList.size() - 1));
                        notifyItemRemoved((arrayList.size() - 1));
                        notifyItemChanged((arrayList.size() - 1), getItemCount());
                    } else if (verhicle.getType() == 0 && arrayList.get(0).getType() > 1000) {
                        arrayList.remove(0);
                        notifyItemRemoved(0);
                        notifyItemChanged(0, getItemCount());
                    }

                    arrayList.add((i + 1), verhicle);
                    notifyItemInserted((i + 1));
                    notifyItemChanged((i + 1), getItemCount());

                    clearDefault(verhicle);
                    return;
                }
            }

            arrayList.add(verhicle);
            clearDefault(verhicle);
            sortVerhicle();
        }
    }

    private void clearDefault(Verhicle verhicle) {
        for (int i = (arrayList.size() - 1); i >= 0; i--) {
            if (arrayList.get(i).getId() != verhicle.getId())
                arrayList.get(i).setFlag_default(0);
        }

        notifyDataSetChanged();
    }

    private void sortVerhicle() {
        Toast.makeText(activity, "sort", Toast.LENGTH_SHORT).show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = (arrayList.size() - 1); i >= 0; i--) {
                    if (arrayList.get(i).getType() > 1000)
                        arrayList.remove(i);
                }

                Collections.sort(arrayList, new Comparator<Verhicle>() {
                    @Override
                    public int compare(Verhicle lhs, Verhicle rhs) {
                        try {
                            return String.valueOf(lhs.getType()).compareTo(String.valueOf(rhs.getType()));
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                if (arrayList.size() > 0) {
                    if (arrayList.get(0).getType() == 1) {
                        arrayList.add(0, new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
                    } else {
                        arrayList.add(0, new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
                    }

                    for (int i = (arrayList.size() - 1); i > 0; i--) {
                        if (arrayList.get((i - 1)).getType() < 10)
                            if (arrayList.get(i).getType() != arrayList.get((i - 1)).getType()) {
                                if (arrayList.get(0).getType() == 1) {
                                    arrayList.add(i, new Verhicle(0, null, 1111, "Ô tô", null, 0, null));
                                } else {
                                    arrayList.add(i, new Verhicle(0, null, 2222, "Xe máy", null, 0, null));
                                }
                                break;
                            }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.execute();
    }

    public void insertNewItem(int position, Verhicle verhicle) {
        if (position == -1) {
            insertItem(verhicle);
        } else {
            updateItem(position, verhicle);
        }
    }
}