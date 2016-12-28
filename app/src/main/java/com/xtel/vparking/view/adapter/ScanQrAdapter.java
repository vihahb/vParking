package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Verhicle;

import java.util.ArrayList;

/**
 * Created by Mr. M.2 on 12/26/2016.
 */

public class ScanQrAdapter extends BaseAdapter {
    private ArrayList<Verhicle> arrayList;
    LayoutInflater inflater;

    public ScanQrAdapter(Activity activity, ArrayList<Verhicle> arrayList) {
        this.arrayList = arrayList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolderDropdown viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_scanqr_spinner_dropdown_item, parent, false);
            viewHolder = new ViewHolderDropdown();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.text2);
            viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.item_scanqr_icon);
            viewHolder.img_default = (ImageView) convertView.findViewById(R.id.item_scanqr_default);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderDropdown) convertView.getTag();
        }

        if (arrayList.get(position).getType() == 1)
            viewHolder.img_icon.setImageResource(R.drawable.ic_action_car);
        else if (arrayList.get(position).getType() == 2)
            viewHolder.img_icon.setImageResource(R.drawable.ic_action_moto);
        else if (arrayList.get(position).getType() == 3)
            viewHolder.img_icon.setImageResource(R.drawable.ic_action_bike);
        else
            viewHolder.img_icon.setVisibility(View.GONE);

        viewHolder.textView.setText(arrayList.get(position).getName());

        if (arrayList.get(position).getFlag_default() == 1)
            viewHolder.img_default.setVisibility(View.VISIBLE);
        else
            viewHolder.img_default.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_spinner_normal, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.text1);
            viewHolder.textView.setTextColor(Color.parseColor("#FFFFFF"));
            viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp, 0);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(arrayList.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        private TextView textView;
    }

    private class ViewHolderDropdown {
        private TextView textView;
        private ImageView img_default, img_icon;
    }
}
