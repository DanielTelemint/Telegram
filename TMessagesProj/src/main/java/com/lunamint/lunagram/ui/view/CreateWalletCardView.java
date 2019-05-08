package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.CreateAccountActivity;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class CreateWalletCardView extends CardView {

    public CreateWalletCardView(Context context, int type, OnClickListener onClickListener) {
        super(context);

        setId(type);
        setOnClickListener(onClickListener);

        setBackgroundResource(R.drawable.bg_card_white_radius12);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(Gravity.CENTER_VERTICAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ImageView icImageview = new ImageView(context);
        icImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        mainLayout.addView(icImageview, LayoutHelper.createLinear(40, 40, 0, 0, 18, 0));

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(textLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.onyx));
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        textLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.languid_lavender));
        descTextview.setTextSize(0, AndroidUtilities.dp(10));
        textLayout.addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
            icImageview.setImageResource(R.drawable.ic_create_wallet);
            titleTextview.setText(LocaleController.getString("createWallet", R.string.createWallet));
            descTextview.setText(LocaleController.getString("generateNewSeed", R.string.generateNewSeed));
        } else {
            icImageview.setImageResource(R.drawable.ic_import_wallet);
            titleTextview.setText(LocaleController.getString("importExistingWallet", R.string.importExistingWallet));
            descTextview.setText(LocaleController.getString("importWordSeed", R.string.importWordSeed));
        }
    }
}
