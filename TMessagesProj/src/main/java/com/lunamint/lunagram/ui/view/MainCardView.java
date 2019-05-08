package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.support.v7.widget.CardView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class MainCardView extends CardView {

    public static final int BUTTON_UPDATE = 1;
    public static final int BUTTON_CREATE_WALLET = 2;
    public static final int BUTTON_SEND = 3;
    public static final int BUTTON_ACTION_ADDRESS = 4;

    private BalanceCardView balanceCardView;
    private EmptyAccountCardView emptyAccountCardView;
    private MainStatusCardView mainStatusCardView;

    public MainCardView(Context context, OnClickListener onClickListener) {
        super(context);

        setBackgroundResource(R.drawable.bg_card_blue);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        balanceCardView = new BalanceCardView(context, onClickListener);
        emptyAccountCardView = new EmptyAccountCardView(context, onClickListener);
        mainStatusCardView = new MainStatusCardView(context, onClickListener);

        balanceCardView.setVisibility(GONE);
        emptyAccountCardView.setVisibility(GONE);
        mainStatusCardView.setVisibility(VISIBLE);

        addView(balanceCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        addView(emptyAccountCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        addView(mainStatusCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void updateBalance(AccountInfo accountInfo, Coin availableCoin) {
        if (balanceCardView == null) return;
        balanceCardView.setVisibility(VISIBLE);
        emptyAccountCardView.setVisibility(GONE);
        mainStatusCardView.setVisibility(GONE);
        balanceCardView.updateBalance(accountInfo, availableCoin);
    }

    public void updateStaking(Coin stakingCoin){
        balanceCardView.updateStaking(stakingCoin);
    }

    public void showEmptyAccountView() {
        if (balanceCardView == null) return;
        balanceCardView.setVisibility(GONE);
        emptyAccountCardView.setVisibility(VISIBLE);
        mainStatusCardView.setVisibility(GONE);
    }

    public void showLoading() {
        if (balanceCardView == null) return;
        balanceCardView.setVisibility(GONE);
        emptyAccountCardView.setVisibility(GONE);
        mainStatusCardView.setVisibility(VISIBLE);
        mainStatusCardView.showLoading();
        setBackgroundResource(R.drawable.bg_card_blue);
    }

    public void showError(String msg) {
        if (balanceCardView == null) return;
        balanceCardView.setVisibility(GONE);
        emptyAccountCardView.setVisibility(GONE);
        mainStatusCardView.setVisibility(VISIBLE);
        mainStatusCardView.showError(msg);
        setBackgroundResource(R.drawable.bg_card_grey);
    }
}
