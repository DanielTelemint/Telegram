package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class StakingToolsCardView extends CardView {

    public StakingToolsCardView(Context context, OnClickListener onClickListener) {
        super(context);

        setBackgroundResource(R.drawable.bg_card_white_radius12);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int icons[] = {R.drawable.ic_my_validators, R.drawable.ic_all_validators, R.drawable.ic_history};
        String titles[] = {LocaleController.getString("myValidators", R.string.myValidators), LocaleController.getString("allValidators", R.string.allValidators), LocaleController.getString("history", R.string.history)};
        for (int i = 0; titles.length > i; i++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setId(i);
            linearLayout.setOnClickListener(onClickListener);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(13), AndroidUtilities.dp(20), AndroidUtilities.dp(13));
            mainLayout.addView(linearLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            ImageView iconImageView = new ImageView(context);
            iconImageView.setImageResource(icons[i]);
            linearLayout.addView(iconImageView, LayoutHelper.createLinear(30, 30, 0, 0, 10, 0));

            TextView titleTextview = new TextView(context);
            titleTextview.setText(titles[i]);
            titleTextview.setTextSize(0, AndroidUtilities.dp(16));
            titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.onyx));
            linearLayout.addView(titleTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 10, 0));

            ImageView arrowImageView = new ImageView(context);
            arrowImageView.setImageResource(R.drawable.arrow_grey);
            linearLayout.addView(arrowImageView, LayoutHelper.createLinear(7, 11));

            if (i < titles.length - 1) {
                ImageView line = new ImageView(context);
                line.setBackgroundColor(0x88E4E9FE);
                mainLayout.addView(line, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1, 20, 0, 20, 0));
            }
        }
    }
}
