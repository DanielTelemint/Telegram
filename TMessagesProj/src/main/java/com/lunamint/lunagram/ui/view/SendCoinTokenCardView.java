package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class SendCoinTokenCardView extends CardView {

    private TextView nameTextview;
    private TextView amountTextview;

    public SendCoinTokenCardView(Context context, Coin coin, OnClickListener onClickListener) {
        super(context);

        setOnClickListener(onClickListener);

        setBackgroundResource(R.drawable.btn_radius8_dark_blue);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(8));
        setCardElevation(UiUtil.getDefaultElevation());

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(Gravity.CENTER_VERTICAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(14), AndroidUtilities.dp(20), AndroidUtilities.dp(14));
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nameTextview = new TextView(context);
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        nameTextview.setTextSize(0, AndroidUtilities.dp(20));
        nameTextview.setTypeface(Typeface.DEFAULT_BOLD);
        mainLayout.addView(nameTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 6, 0));

        amountTextview = new TextView(context);
        amountTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        amountTextview.setTextSize(0, AndroidUtilities.dp(16));
        amountTextview.setTypeface(Typeface.DEFAULT_BOLD);
        mainLayout.addView(amountTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 6, 0));

        ImageView arrowImageView = new ImageView(context);
        arrowImageView.setImageResource(R.drawable.arrow_grey);
        mainLayout.addView(arrowImageView, LayoutHelper.createLinear(7, 11));

        update(coin);
    }

    public void update(Coin coin) {
        if (nameTextview == null || coin == null) return;
        nameTextview.setText(coin.getDenomDisplayName());
        amountTextview.setText(NumberFormatter.getNumber(coin.getAmount()));
    }
}
