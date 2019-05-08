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
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TelegramTransferOnboardingView extends LinearLayout {

    public TelegramTransferOnboardingView(Context context) {
        super(context);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("telegramTransfer", R.string.telegramTransfer));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 30));

        ImageView warningImgview0 = new ImageView(context);
        addView(warningImgview0, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        TextView warningTextview0 = new TextView(context);
        warningTextview0.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        warningTextview0.setTextSize(0, AndroidUtilities.dp(14));
        addView(warningTextview0, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        ImageView arrow0 = new ImageView(context);
        arrow0.setImageResource(R.drawable.create_account_guide_arrow_down);
        addView(arrow0, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        ImageView warningImgview1 = new ImageView(context);
        addView(warningImgview1, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        TextView warningTextview1 = new TextView(context);
        warningTextview1.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        warningTextview1.setTextSize(0, AndroidUtilities.dp(14));
        addView(warningTextview1, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        ImageView arrow1 = new ImageView(context);
        arrow1.setImageResource(R.drawable.create_account_guide_arrow_down);
        addView(arrow1, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        ImageView warningImgview2 = new ImageView(context);
        addView(warningImgview2, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        TextView warningTextview2 = new TextView(context);
        warningTextview2.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        warningTextview2.setTextSize(0, AndroidUtilities.dp(14));
        addView(warningTextview2, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 50));

        descTextview.setText(LocaleController.getString("telegramTransferDesc", R.string.telegramTransferDesc));
        warningImgview0.setImageResource(R.drawable.create_account_guide1);
        warningTextview0.setText(LocaleController.getString("telegramTransferGuide1", R.string.telegramTransferGuide1));
        warningImgview1.setImageResource(R.drawable.create_account_guide2);
        warningTextview1.setText(LocaleController.getString("telegramTransferGuide2", R.string.telegramTransferGuide2));
        warningImgview2.setImageResource(R.drawable.create_account_guide3);
        warningTextview2.setText(LocaleController.getString("telegramTransferGuide3", R.string.telegramTransferGuide3));
    }
}
