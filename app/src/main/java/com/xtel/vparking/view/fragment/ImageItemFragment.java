package com.xtel.vparking.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;

/**
 * Created by Lê Công Long Vũ on 11/10/2016.
 */

public class ImageItemFragment extends Fragment {
    private ImageView imageView;
    private String url;

    public static ImageItemFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(Constants.PK_IMAGE, url);
        ImageItemFragment fragment = new ImageItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_imageview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imageView == null)
            imageView = (ImageView) view.findViewById(R.id.item_imageview);

        url = getArguments().getString(Constants.PK_IMAGE);

        if (url != null)
            Picasso.with(getContext())
                    .load(url)
                    .noPlaceholder()
                    .error(R.mipmap.ic_parking_background)
                    .into(imageView);
        else
            imageView.setImageResource(R.mipmap.ic_parking_background);

    }
}
