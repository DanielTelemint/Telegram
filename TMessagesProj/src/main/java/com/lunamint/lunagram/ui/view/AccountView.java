package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.AccountInfo;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class AccountView extends LinearLayout {

    public static final int BUTTON_SETTING_WALLET = 1;
    public static final int BUTTON_CHANGE_CHAIN = 2;

    private TextView accountNameTextview;
    private ImageView accountArrowImgview;

    public AccountView(Context context, OnClickListener onClickListener) {
        super(context);

        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.HORIZONTAL);

        setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(20));

        LinearLayout accountLayout = new LinearLayout(context);
        accountLayout.setId(AccountView.BUTTON_SETTING_WALLET);
        accountLayout.setOnClickListener(onClickListener);
        accountLayout.setOrientation(HORIZONTAL);
        accountLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(accountLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 6, 0));

        accountNameTextview = new TextView(context);
        accountNameTextview.setTextColor(ActivityCompat.getColor(context, R.color.violet_blue));
        accountNameTextview.setTextSize(0, AndroidUtilities.dp(20));
        accountNameTextview.setGravity(Gravity.CENTER);
        accountNameTextview.setTypeface(Typeface.DEFAULT_BOLD);
        accountLayout.addView(accountNameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 5, 0));

        accountArrowImgview = new ImageView(context);
        accountArrowImgview.setScaleType(ImageView.ScaleType.FIT_XY);
        accountArrowImgview.setImageResource(R.drawable.ic_down_arrow);
        accountLayout.addView(accountArrowImgview, LayoutHelper.createLinear(18, 18));
        accountArrowImgview.setVisibility(INVISIBLE);

        LinearLayout chainIdLayout = new LinearLayout(context);
        chainIdLayout.setId(AccountView.BUTTON_CHANGE_CHAIN);
        chainIdLayout.setOnClickListener(onClickListener);
        chainIdLayout.setOrientation(HORIZONTAL);
        chainIdLayout.setGravity(Gravity.CENTER);
        chainIdLayout.setBackgroundResource(R.drawable.bg_card_white_radius12);
        chainIdLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(6), AndroidUtilities.dp(10), AndroidUtilities.dp(6));
        addView(chainIdLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView chainTextview = new TextView(context);
        chainTextview.setTextColor(ActivityCompat.getColor(context, R.color.medium_slate_blue));
        chainTextview.setTextSize(0, AndroidUtilities.dp(10));
        chainTextview.setText(Blockchain.getInstance().getChainId());
        chainIdLayout.addView(chainTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 9, 0));

        ImageView chainIdArrowImgview = new ImageView(context);
        chainIdArrowImgview.setScaleType(ImageView.ScaleType.FIT_XY);
        chainIdArrowImgview.setImageResource(R.drawable.ic_arrow_right_blue);
        chainIdLayout.addView(chainIdArrowImgview, LayoutHelper.createLinear(3, 6));
    }

    public void update(AccountInfo data) {
        if (accountNameTextview == null) return;
        if (data == null) {
            accountArrowImgview.setVisibility(INVISIBLE);
            accountNameTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.manatee));
            accountNameTextview.setText("(NO WALLET)");
        } else {
            accountArrowImgview.setVisibility(VISIBLE);
            accountNameTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.violet_blue));
            accountNameTextview.setText(data.getName());
        }
    }

}
