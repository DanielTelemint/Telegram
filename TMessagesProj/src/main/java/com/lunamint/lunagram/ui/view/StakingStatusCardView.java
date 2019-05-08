package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class StakingStatusCardView extends CardView {

    private TextView amountTextviews[] = new TextView[2];
    private TextView denomTextviews[] = new TextView[2];

    private LinearLayout progressLayout;
    private LinearLayout mainLayout;

    public StakingStatusCardView(Context context, OnClickListener onClickListener) {
        super(context);

        setBackgroundResource(R.drawable.bg_card_white_radius12);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        progressLayout = new LinearLayout(context);
        progressLayout.setGravity(Gravity.CENTER);
        frameLayout.addView(progressLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(context);
        progressLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20), 0);
        frameLayout.addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        mainLayout.setVisibility(INVISIBLE);

        int balanceTitles[] = {R.string.staking, R.string.unstaking};
        for (int i = 0; balanceTitles.length > i; i++) {
            LinearLayout balanceLayout = new LinearLayout(context);
            balanceLayout.setId(i);
            balanceLayout.setOnClickListener(onClickListener);
            balanceLayout.setOrientation(LinearLayout.HORIZONTAL);
            balanceLayout.setGravity(Gravity.CENTER_VERTICAL);
            balanceLayout.setPadding(0, AndroidUtilities.dp(14), 0, AndroidUtilities.dp(14));
            mainLayout.addView(balanceLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView titleTextview = new TextView(context);
            titleTextview.setText(context.getString(balanceTitles[i]));
            titleTextview.setTextSize(0, AndroidUtilities.dp(12));
            titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
            balanceLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 10, 0));

            LinearLayout amountLayout = new LinearLayout(context);
            amountLayout.setOrientation(LinearLayout.VERTICAL);
            amountLayout.setGravity(Gravity.RIGHT);
            balanceLayout.addView(amountLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

            amountTextviews[i] = new TextView(context);
            amountTextviews[i].setTextSize(0, AndroidUtilities.dp(24));
            amountTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
            amountTextviews[i].setTypeface(Typeface.DEFAULT_BOLD);
            amountLayout.addView(amountTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            denomTextviews[i] = new TextView(context);
            denomTextviews[i].setTextSize(0, AndroidUtilities.dp(12));
            denomTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
            amountLayout.addView(denomTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            if (i == 0) {
                ImageView line = new ImageView(context);
                line.setBackgroundColor(0x88E4E9FE);
                mainLayout.addView(line, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));
            }
        }
    }

    public void updateStaking(Coin coin) {
        if(amountTextviews == null || amountTextviews[0] == null) return;
        if (coin != null) {
            amountTextviews[0].setText(NumberFormatter.getNumber(coin.getAmount()));
            denomTextviews[0].setText(coin.getDenomDisplayName());
        } else {
            amountTextviews[0].setText("0");
            denomTextviews[0].setText(Blockchain.getInstance().getReserveDisplayName());
        }
        update();
    }

    public void updateUnstaking(Coin coin) {
        if(amountTextviews == null || amountTextviews[0] == null) return;
        if (coin != null) {
            amountTextviews[1].setText(NumberFormatter.getNumber(coin.getAmount()));
            denomTextviews[1].setText(coin.getDenomDisplayName());
        } else {
            amountTextviews[1].setText("0");
            denomTextviews[1].setText(Blockchain.getInstance().getReserveDisplayName());
        }
        update();
    }

    private void update(){
        if(progressLayout == null) return;
        progressLayout.setVisibility(GONE);
        mainLayout.setVisibility(VISIBLE);
    }
}
