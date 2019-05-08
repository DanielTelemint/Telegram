package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.Parser;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class BalanceCardView extends LinearLayout {

    private TextView addressTextview;
    private TextView amountTextviews[] = new TextView[2];
    private TextView denomTextviews[] = new TextView[2];

    public BalanceCardView(Context context, OnClickListener onClickListener) {
        super(context);

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout addressLayout = new LinearLayout(context);
        addressLayout.setOrientation(LinearLayout.HORIZONTAL);
        addressLayout.setGravity(Gravity.CENTER_VERTICAL);
        addressLayout.setId(MainCardView.BUTTON_ACTION_ADDRESS);
        addressLayout.setBackgroundResource(R.drawable.bg_grey_radius12_alpha11);
        addressLayout.setOnClickListener(onClickListener);
        mainLayout.addView(addressLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 4));

        addressTextview = new TextView(context);
        addressTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        addressTextview.setPadding(0, AndroidUtilities.dp(6), 0, AndroidUtilities.dp(6));
        addressTextview.setTextSize(0, AndroidUtilities.dp(12));
        addressLayout.addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 0, 10, 0));

        ImageView copyImgview = new ImageView(context);
        copyImgview.setScaleType(ImageView.ScaleType.FIT_XY);
        copyImgview.setImageResource(R.drawable.ic_copy);
        addressLayout.addView(copyImgview, LayoutHelper.createLinear(8, 9, 0, 0, 16, 0));


        int balanceTitles[] = {R.string.amount, R.string.staking};
        for (int i = 0; balanceTitles.length > i; i++) {
            LinearLayout balanceLayout = new LinearLayout(context);
            balanceLayout.setOrientation(LinearLayout.HORIZONTAL);
            balanceLayout.setGravity(Gravity.CENTER_VERTICAL);
            balanceLayout.setPadding(0, AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10));
            mainLayout.addView(balanceLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView titleTextview = new TextView(context);
            titleTextview.setText(context.getString(balanceTitles[i]));
            titleTextview.setTextSize(0, AndroidUtilities.dp(12));
            titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
            balanceLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 10, 0));

            LinearLayout amountLayout = new LinearLayout(context);
            amountLayout.setOrientation(LinearLayout.VERTICAL);
            amountLayout.setGravity(Gravity.RIGHT);
            balanceLayout.addView(amountLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

            amountTextviews[i] = new TextView(context);
            amountTextviews[i].setTextSize(0, AndroidUtilities.dp(24));
            amountTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.white));
            amountTextviews[i].setTypeface(Typeface.DEFAULT_BOLD);
            amountTextviews[i].setPadding(0, 0, 0, 0);
            amountLayout.addView(amountTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            denomTextviews[i] = new TextView(context);
            denomTextviews[i].setTextSize(0, AndroidUtilities.dp(12));
            denomTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
            denomTextviews[i].setPadding(0, 0, 0, 0);
            amountLayout.addView(denomTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, -6, 0, 0));

            if (i == 0) {
                ImageView line = new ImageView(context);
                line.setBackgroundColor(0x1AE4E9FE);
                mainLayout.addView(line, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));
            }
        }

        Button sendButton = new Button(context);
        sendButton.setId(MainCardView.BUTTON_SEND);
        sendButton.setGravity(Gravity.CENTER);
        sendButton.setOnClickListener(onClickListener);
        sendButton.setBackgroundResource(R.drawable.btn_radius24_alpha20_black);
        sendButton.setTextSize(0, AndroidUtilities.dp(16));
        sendButton.setTypeface(Typeface.DEFAULT_BOLD);
        sendButton.setTextColor(ActivityCompat.getColor(context, R.color.white));
        sendButton.setText(LocaleController.getString("send", R.string.send));
        mainLayout.addView(sendButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 0, 4, 0, 0));
    }

    public void updateBalance(AccountInfo data, Coin availableCoin) {
        if (addressTextview == null || data == null) return;
        addressTextview.setText(Parser.getShotAddressForDisplay(data.getAddress()));

        if (availableCoin != null) {
            amountTextviews[0].setText(NumberFormatter.getNumber(availableCoin.getAmount()));
            denomTextviews[0].setText(availableCoin.getDenomDisplayName());
        } else {
            amountTextviews[0].setText("0");
            denomTextviews[0].setText(Blockchain.getInstance().getReserveDisplayName());
        }
    }

    public void updateStaking(Coin stakingCoin) {
        if (stakingCoin != null) {
            amountTextviews[1].setText(NumberFormatter.getNumber(stakingCoin.getAmount()));
            denomTextviews[1].setText(stakingCoin.getDenomDisplayName());
        } else {
            amountTextviews[1].setText("0");
            denomTextviews[1].setText(Blockchain.getInstance().getReserveDisplayName());
        }
    }
}
