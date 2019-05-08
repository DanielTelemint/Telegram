package com.lunamint.lunagram.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ClaimRewardCardView extends CardView {

    private LinearLayout loadingLayout;
    private LinearLayout mainLayout;
    private TextView amountTextview;
    private TextView denomTextview;

    public ClaimRewardCardView(Context context, OnClickListener onClickListener) {
        super(context);

        setBackgroundResource(R.drawable.bg_card_blue);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        loadingLayout = new LinearLayout(context);
        loadingLayout.setGravity(Gravity.CENTER);
        addView(loadingLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(context);
        loadingLayout.addView(progressBar);

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        mainLayout.setVisibility(INVISIBLE);

        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        infoLayout.setGravity(Gravity.CENTER_VERTICAL);
        infoLayout.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
        mainLayout.addView(infoLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView titleTextview = new TextView(context);
        titleTextview.setTextSize(0, AndroidUtilities.dp(12));

        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
        titleTextview.setText(LocaleController.getString("reward", R.string.reward));
        infoLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 20, 0));

        LinearLayout amountLayout = new LinearLayout(context);
        amountLayout.setOrientation(LinearLayout.VERTICAL);
        amountLayout.setGravity(Gravity.RIGHT);
        infoLayout.addView(amountLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.f));

        amountTextview = new TextView(context);
        amountTextview.setTextSize(0, AndroidUtilities.dp(24));
        amountTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        amountTextview.setTypeface(Typeface.DEFAULT_BOLD);
        amountLayout.addView(amountTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        denomTextview = new TextView(context);
        denomTextview.setTextSize(0, AndroidUtilities.dp(12));
        denomTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
        amountLayout.addView(denomTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        Button button = new Button(context);
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(onClickListener);
        button.setBackgroundResource(R.drawable.btn_radius24_alpha20_black);
        button.setTextSize(0, AndroidUtilities.dp(16));
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setTextColor(ActivityCompat.getColor(context, R.color.white));
        button.setText(LocaleController.getString("claimReward", R.string.claimReward));
        mainLayout.addView(button, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 0, 10, 0, 0));
    }

    public void update(Coin coin) {
        if (coin == null || amountTextview == null) return;
        mainLayout.setVisibility(VISIBLE);
        loadingLayout.setVisibility(GONE);

        denomTextview.setText(coin.getDenomDisplayName());

        try {
            float oldAmount = 0;
            if (!amountTextview.getText().toString().equals("")) {
                oldAmount = Float.parseFloat(amountTextview.getText().toString().replaceAll(",", ""));
            }

            if (oldAmount == 0) {
                amountTextview.setText(NumberFormatter.getNumber(coin.getAmount()));
                return;
            }

            float newAmount = Float.parseFloat(coin.getAmount());

            if (oldAmount == newAmount) return;

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(oldAmount, newAmount);
            valueAnimator.setDuration(800);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    amountTextview.setText(NumberFormatter.getNumberWithFixedDecimal(animation.getAnimatedValue().toString()));
                }
            });
            valueAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
            amountTextview.setText(NumberFormatter.getNumber(coin.getAmount()));
        }
    }
}
