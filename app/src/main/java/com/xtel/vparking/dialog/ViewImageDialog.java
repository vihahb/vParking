package com.xtel.vparking.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xtel.vparking.R;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.view.adapter.ViewImageBottomSheetdapter;

import java.util.ArrayList;

/**
 * Created by Vulcl on 2/15/2017
 */

public class ViewImageDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MaterialDialogSheet);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getDialog().setCancelable(true);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        getDialog().getWindow().setGravity(Gravity.CENTER);

        return inflater.inflate(R.layout.dialog_view_image, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT);
//        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> arrayList = getArguments().getStringArrayList(Constants.PK_IMAGE);

        if (arrayList == null) {
            Toast.makeText(getContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        initViewPager(view, arrayList);
        initButton(view);
    }

    private void initViewPager(View view, ArrayList<String> arrayList) {
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.dialog_view_image_viewpager);
        ViewImageBottomSheetdapter parkingDetailAdapter = new ViewImageBottomSheetdapter(getChildFragmentManager(), arrayList);
        mViewPager.setAdapter(parkingDetailAdapter);
    }

    private void initButton(View view) {
        Button button = (Button) view.findViewById(R.id.dialog_view_image_close);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}