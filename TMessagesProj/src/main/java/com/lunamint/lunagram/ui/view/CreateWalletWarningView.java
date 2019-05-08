package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.CreateAccountActivity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class CreateWalletWarningView extends LinearLayout {

    public CreateWalletWarningView(Context context, int type) {
        super(context);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.coral_red));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("warning", R.string.warning) + "!");
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

        ImageView arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.create_account_guide_arrow_down);
        addView(arrow, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        ImageView warningImgview1 = new ImageView(context);
        addView(warningImgview1, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        TextView warningTextview1 = new TextView(context);
        warningTextview1.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        warningTextview1.setTextSize(0, AndroidUtilities.dp(14));
        addView(warningTextview1, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 40));

        TextView warningTextview2 = new TextView(context);
        warningTextview2.setTextColor(ActivityCompat.getColor(context, R.color.red));
        warningTextview2.setTextSize(0, AndroidUtilities.dp(14));
        warningTextview2.setGravity(Gravity.CENTER);
        addView(warningTextview2, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 50));

        if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
            descTextview.setText(LocaleController.getString("warningSeed1", R.string.warningSeed1));
            warningImgview0.setImageResource(R.drawable.create_account_warning1);
            warningTextview0.setText(LocaleController.getString("createWalletWarning1", R.string.createWalletWarning1));
            warningImgview1.setImageResource(R.drawable.create_account_warning2);
            warningTextview1.setText(LocaleController.getString("createWalletWarning2", R.string.createWalletWarning2));
            warningTextview2.setText(LocaleController.getString("createWalletWarning3", R.string.createWalletWarning3));
        } else {
            descTextview.setText(LocaleController.getString("warningSeed2", R.string.warningSeed2));
            warningImgview0.setImageResource(R.drawable.import_account_warning1);
            warningTextview0.setText(LocaleController.getString("createWalletWarning1", R.string.importWalletWarning1));
            warningImgview1.setImageResource(R.drawable.import_account_warning2);
            warningTextview1.setText(LocaleController.getString("createWalletWarning2", R.string.importWalletWarning2));
            warningTextview2.setText(LocaleController.getString("createWalletWarning3", R.string.importWalletWarning3));
        }
    }
}
