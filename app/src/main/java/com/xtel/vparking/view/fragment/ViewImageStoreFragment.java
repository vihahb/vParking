package com.xtel.vparking.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.dialog.ViewImageDialog;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 11/10/2016
 */

public class ViewImageStoreFragment extends Fragment {
    private ImageView imageView;
    private ProgressBar progressBar;
    private ArrayList<String> arrayList;

    public static ViewImageStoreFragment newInstance(ArrayList<String> arrayList, int position) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.PK_IMAGE, arrayList);
        args.putInt(Constants.PK_POSITION, position);
        ViewImageStoreFragment fragment = new ViewImageStoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_imageview, container, false);

        arrayList = getArguments().getStringArrayList(Constants.PK_IMAGE);
        int position = getArguments().getInt(Constants.PK_POSITION);

        initView(view);
        initData(arrayList.get(position));
        initListener();

        return view;
    }

    private void initView(View view) {
        if (imageView == null)
            imageView = (ImageView) view.findViewById(R.id.item_imageview);
        if (progressBar == null)
            progressBar = (ProgressBar) view.findViewById(R.id.item_progress);
    }

    private void initData(String url) {
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5c5ca7"), android.graphics.PorterDuff.Mode.MULTIPLY);

        if (url != null)
            Picasso.with(getContext())
                    .load(url)
                    .noPlaceholder()
                    .error(R.mipmap.ic_parking_background)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        else
            imageView.setImageResource(R.mipmap.ic_parking_background);
    }

    private void initListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable(Constants.PK_IMAGE, arrayList);

                ViewImageDialog customDailog=new ViewImageDialog();
                customDailog.setArguments(args);
                customDailog.show(getFragmentManager(),"addons_fragment");
            }
        });
    }
}