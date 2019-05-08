package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.CreateAccountActivity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class SelectWalletTypeFormView extends LinearLayout {

    public SelectWalletTypeFormView(Context context, OnClickListener onClickListener) {
        super(context);

        setOrientation(VERTICAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setText(LocaleController.getString("createWallet", R.string.createWallet));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(LocaleController.getString("welcomeLunagramWallet", R.string.welcomeLunagramWallet));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 40));

        CreateWalletCardView createWalletCardView = new CreateWalletCardView(context, CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT, onClickListener);
        addView(createWalletCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        CreateWalletCardView importWalletCardView = new CreateWalletCardView(context, CreateAccountActivity.TYPE_IMPORT_EXISTING_WALLET, onClickListener);
        addView(importWalletCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 40));

    }
}
