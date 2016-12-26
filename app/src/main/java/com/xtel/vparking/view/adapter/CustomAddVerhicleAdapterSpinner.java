package com.xtel.vparking.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xtel.vparking.R;
import com.xtel.vparking.model.entity.Brandname;

import java.util.ArrayList;

/**
 * Created by vivhp on 12/11/2016.
 */

public class CustomAddVerhicleAdapterSpinner extends ArrayAdapter<Brandname> {

    private int IdLayout;
    private ArrayList<Brandname> arrayList;
    private Activity context;
    LayoutInflater inflter;

    public CustomAddVerhicleAdapterSpinner(Activity context, int resource, ArrayList<Brandname> arrayList) {
        super(context, resource, arrayList);
        IdLayout = resource;
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            inflter = context.getLayoutInflater();
            row = inflter.inflate(IdLayout, null);
        }
        Brandname brandnameModel = new Brandname();

        TextView tv_code = (TextView) row.findViewById(R.id.tv_brand_code);
        TextView tv_name = (TextView) row.findViewById(R.id.tv_brand_name);
        TextView tv_made = (TextView) row.findViewById(R.id.tv_brand_made);

        //Set value item
        brandnameModel = arrayList.get(position);
        tv_code.setText(brandnameModel.getCode());
        tv_name.setText(brandnameModel.getName());
        tv_name.setTextColor(row.getResources().getColor(R.color.colorPrimary));
        tv_made.setText(brandnameModel.getMadeby());

        return row;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            inflter = context.getLayoutInflater();
            row = inflter.inflate(IdLayout, null);
        }
        Brandname brandnameModel = new Brandname();

        TextView tv_code = (TextView) row.findViewById(R.id.tv_brand_code);
        TextView tv_name = (TextView) row.findViewById(R.id.tv_brand_name);
        TextView tv_made = (TextView) row.findViewById(R.id.tv_brand_made);

        //Set value item
        brandnameModel = arrayList.get(position);
        tv_code.setText(brandnameModel.getCode());
        tv_name.setText(brandnameModel.getName());
        tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_arrow_down, 0);
        tv_made.setText(brandnameModel.getMadeby());

        return row;
    }
}
