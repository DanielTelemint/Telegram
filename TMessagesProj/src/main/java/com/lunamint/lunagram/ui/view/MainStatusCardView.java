package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lunamint.lunagram.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class MainStatusCardView extends LinearLayout {

    private ProgressBar progressBar;
    private ImageView iconImageview;
    private TextView statusTextview;

    public MainStatusCardView(Context context, OnClickListener onClickListener) {
        super(context);

        setId(MainCardView.BUTTON_UPDATE);
        setOnClickListener(onClickListener);

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 200));

        progressBar = new ProgressBar(getContext());
        mainLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        iconImageview = new ImageView(context);
        iconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        iconImageview.setImageResource(R.drawable.ic_warnning_white);
        mainLayout.addView(iconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 28));

        statusTextview = new TextView(context);
        statusTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        statusTextview.setTextSize(0, AndroidUtilities.dp(16));
        statusTextview.setTypeface(Typeface.DEFAULT_BOLD);
        statusTextview.setGravity(Gravity.CENTER);
        mainLayout.addView(statusTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        iconImageview.setVisibility(GONE);
        statusTextview.setVisibility(GONE);
    }

    public void showLoading(){
        if(progressBar == null) return;

        progressBar.setVisibility(VISIBLE);
        iconImageview.setVisibility(GONE);
        statusTextview.setVisibility(GONE);

    }

    public void showError(String msg){
        if(progressBar == null) return;

        progressBar.setVisibility(GONE);
        iconImageview.setVisibility(VISIBLE);
        statusTextview.setVisibility(VISIBLE);
        statusTextview.setText(msg);
    }
}
