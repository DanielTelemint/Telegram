package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class EmptyAccountCardView extends LinearLayout {

    public EmptyAccountCardView(Context context, OnClickListener onClickListener) {
        super(context);

        setId(MainCardView.BUTTON_CREATE_WALLET);
        setOnClickListener(onClickListener);

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 200));

        ImageView plusIconImageview = new ImageView(context);
        plusIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        plusIconImageview.setImageResource(R.drawable.ic_plus);
        mainLayout.addView(plusIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 28));

        TextView titleTextview = new TextView(context);
        titleTextview.setText(context.getText(R.string.createNewAccount));
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setGravity(Gravity.CENTER);
        mainLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }
}
