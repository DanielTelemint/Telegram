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
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.TokenUtil;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class TokensCardView extends CardView {

    private OnClickListener onClickListener;

    private TextView tokenCntTextview;
    private LinearLayout tokensLayout;

    public TokensCardView(Context context, OnClickListener onClickListener) {
        super(context);

        this.onClickListener = onClickListener;

        setBackgroundResource(R.drawable.bg_card_white_radius12);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mainLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(12), AndroidUtilities.dp(20), AndroidUtilities.dp(12));
        mainLayout.addView(titleLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView titleTextview = new TextView(context);
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setText(LocaleController.getString("tokens", R.string.tokens));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 4, 0));

        tokenCntTextview = new TextView(context);
        tokenCntTextview.setTextSize(0, AndroidUtilities.dp(16));
        tokenCntTextview.setTextColor(ActivityCompat.getColor(context, R.color.medium_slate_blue));
        tokenCntTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleLayout.addView(tokenCntTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        tokensLayout = new LinearLayout(context);
        tokensLayout.setOrientation(LinearLayout.VERTICAL);
        tokensLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.addView(tokensLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        setVisibility(GONE);
    }

    public void showLoading() {
        setVisibility(GONE);
    }


    public void update(ArrayList<Coin> coins) {
        if (tokensLayout == null || coins == null) {
            setVisibility(GONE);
            return;
        } else {
            tokensLayout.removeAllViews();
        }

        /*
        for (String token : TokenUtil.DEFAULT_TOKENS) {
            boolean hasToken = false;
            for (int i = 0; coins.size() > i; i++) {
                if (coins.get(i).getDenom().equals(token)) {
                    hasToken = true;
                }
            }

            if (!hasToken) {
                Coin coin = new Coin();
                coin.setDenom(token);
                coin.setAmount("0");
                coins.add(coin);
            }
        }*/

        for (int i = 0; coins.size() > i; i++) {
            if (!coins.get(i).getDenom().equals(Blockchain.getInstance().getReserveDenom())) {
                LinearLayout tokenLayout = new LinearLayout(getContext());
                tokenLayout.setId(i);
                tokenLayout.setOnClickListener(onClickListener);
                tokenLayout.setGravity(Gravity.CENTER_VERTICAL);
                tokenLayout.setOrientation(LinearLayout.HORIZONTAL);
                tokenLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
                tokensLayout.addView(tokenLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

                ImageView tokenImgview = new ImageView(getContext());
                tokenImgview.setImageResource(TokenUtil.getTokenIcon(coins.get(i).getDenom()));
                tokenLayout.addView(tokenImgview, LayoutHelper.createLinear(30, 30, 0, 0, 10, 0));

                LinearLayout tokenInfoLayout = new LinearLayout(getContext());
                tokenInfoLayout.setGravity(Gravity.CENTER_VERTICAL);
                tokenInfoLayout.setOrientation(LinearLayout.VERTICAL);
                tokenLayout.addView(tokenInfoLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

                TextView tokenNameTextView = new TextView(getContext());
                tokenNameTextView.setTextSize(0, AndroidUtilities.dp(16));
                tokenNameTextView.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
                tokenNameTextView.setText(coins.get(i).getDenomDisplayName());
                tokenInfoLayout.addView(tokenNameTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

                String tokenFullname = TokenUtil.getTokenFullname(coins.get(i).getDenom());
                if (tokenFullname != null) {
                    TextView tokenFullnameTextView = new TextView(getContext());
                    tokenFullnameTextView.setTextSize(0, AndroidUtilities.dp(10));
                    tokenFullnameTextView.setTextColor(ActivityCompat.getColor(getContext(), R.color.languid_lavender));
                    tokenFullnameTextView.setText(tokenFullname);
                    tokenInfoLayout.addView(tokenFullnameTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
                }

                TextView tokenBalanceTextView = new TextView(getContext());
                tokenBalanceTextView.setTextSize(0, AndroidUtilities.dp(14));
                tokenBalanceTextView.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
                tokenBalanceTextView.setGravity(Gravity.RIGHT);
                tokenBalanceTextView.setText(NumberFormatter.getNumber(coins.get(i).getAmount()));
                tokenLayout.addView(tokenBalanceTextView, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

                if (i < coins.size() - 1) {
                    ImageView line = new ImageView(getContext());
                    line.setBackgroundColor(0x1AE4E9FE);
                    tokensLayout.addView(line, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1, 20, 0, 20, 0));
                }
            }
        }

        String cnt = (coins.size() - 1) + "";
        tokenCntTextview.setText(cnt);

        setVisibility(VISIBLE);
    }
}
