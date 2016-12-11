package com.xtel.vparking.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Prices;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 12/8/2016.
 */

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private ArrayList<Prices> arrayList;
    private ArrayAdapter adapter_transport, adapter_price;

    @SuppressWarnings("unchecked")
    public PriceAdapter(Context context, ArrayList<Prices> arrayList) {
        adapter_transport = new ArrayAdapter(context, R.layout.item_spinner_narmal, context.getResources().getStringArray(R.array.add_transport_type));
        adapter_transport.setDropDownViewResource(R.layout.item_spinner_dropdown_item);
        adapter_price = new ArrayAdapter(context, R.layout.item_spinner_narmal, context.getResources().getStringArray(R.array.add_price_type));
        adapter_price.setDropDownViewResource(R.layout.item_spinner_dropdown_item);
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_parking_price, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Prices prices = arrayList.get(position);

        holder.sp_for.setAdapter(adapter_transport);
        holder.sp_type.setAdapter(adapter_price);

        holder.sp_type.setSelection((prices.getPrice_type() - 1));
        switch (prices.getPrice_for()) {
            case 1:
                holder.sp_for.setSelection(2);
                break;
            case 2:
                holder.sp_for.setSelection(1);
                break;
            case 3:
                holder.sp_for.setSelection(0);
                break;
            default:
                break;
        }

        if (prices.getPrice() > 0)
            holder.edt_price.setText(String.valueOf(prices.getPrice()));
        else
            holder.edt_price.setText("");

        if (position == (arrayList.size() - 1))
            holder.img_add.setImageResource(R.drawable.ic_add_box_white_36dp);
        else
            holder.img_add.setImageResource(R.mipmap.ic_close_box);

//        final TextWatcher textWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (holder.edt_price.isFocused()) {
//                    Log.e("price", "add text " + position);
////                    Log.e("price", "add text to " + position + "    " + arrayList.size());
////
////                    int money;
////                    if (s != null && !s.toString().isEmpty())
////                        money = Integer.parseInt(s.toString());
////                    else
////                        money = 0;
////
////                    arrayList.get(position).setPrice(money);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        };

        try {
            holder.edt_price.removeTextChangedListener(holder.textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.edt_price.addTextChangedListener(holder.textWatcher);

        holder.sp_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    arrayList.get(position).setPrice_for(3);
                } else if (pos == 1) {
                    arrayList.get(position).setPrice_for(2);
                } else if (pos == 2) {
                    arrayList.get(position).setPrice_for(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                arrayList.get(position).setPrice_type((pos + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == (arrayList.size() - 1)) {
                    holder.img_add.setImageResource(R.mipmap.ic_close_box);

                    arrayList.add(new Prices(0, 1, 3));
                    notifyItemInserted((arrayList.size() - 1));
                    notifyItemRangeChanged((arrayList.size() - 1), getItemCount());
                } else {
                    arrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, arrayList.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Spinner sp_type, sp_for;
        private EditText edt_price;
        private ImageView img_add;
        private TextWatcher textWatcher;

        ViewHolder(final View itemView) {
            super(itemView);
            edt_price = (EditText) itemView.findViewById(R.id.item_edt_add_parking_money);
            sp_type = (Spinner) itemView.findViewById(R.id.item_sp_add_parking_time_type);
            sp_for = (Spinner) itemView.findViewById(R.id.item_sp_add_parking_transport_type);
            img_add = (ImageView) itemView.findViewById(R.id.item_img_add_parking_add);

            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edt_price.isFocused()) {
                        Log.e("price", "add text " + getAdapterPosition());

                        int money;
                        if (s != null && !s.toString().isEmpty())
                            money = Integer.parseInt(s.toString());
                        else
                            money = 0;

                        arrayList.get(getAdapterPosition()).setPrice(money);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };
        }
    }

    public ArrayList<Prices> getArrayList() {
        return this.arrayList;
    }
}
