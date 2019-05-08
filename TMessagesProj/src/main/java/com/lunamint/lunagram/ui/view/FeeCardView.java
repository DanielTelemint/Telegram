package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class FeeCardView extends CardView {

    private LinearLayout mainLayout;
    private TextView titleTextview;
    private TextView feeTextview;

    public FeeCardView(Context context, boolean isSelected, String title, String fee, String denom) {
        super(context);

        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 72));

        titleTextview = new TextView(context);
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(title);
        mainLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        feeTextview = new TextView(context);
        feeTextview.setTextSize(0, AndroidUtilities.dp(12));
        feeTextview.setText(fee + " " + denom);
        mainLayout.addView(feeTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        update(isSelected);
    }

    public void update(boolean isSelected) {
        if (mainLayout == null) return;
        if (isSelected) {
            setBackgroundResource(R.drawable.bg_card_blue);
            titleTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.white));
            feeTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.white));
        } else {
            setBackgroundResource(R.drawable.bg_card_white);
            titleTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.manatee));
            feeTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.manatee));
        }

        mainLayout.setPadding(AndroidUtilities.dp(15), 0, AndroidUtilities.dp(15), 0);
    }
}
