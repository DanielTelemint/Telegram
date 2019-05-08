package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lunamint.lunagram.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TxConfirmView extends LinearLayout {

    public final static int TYPE_SEND = 0;
    public final static int TYPE_STAKE = 1;
    public final static int TYPE_UNSTAKE = 2;
    public final static int TYPE_REDELEGATE = 3;
    public final static int TYPE_VOTE = 4;
    public final static int TYPE_CLAIM_REWARDS = 5;

    private TextView errorTextview;
    private ProgressBar progressBar;

    public TxConfirmView(Context context, String title) {
        super(context);

        setOrientation(VERTICAL);
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(title);
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 10, 20, 0));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(LocaleController.getString("doubleCheckAction", R.string.doubleCheckAction));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 0, 20, 36));

        errorTextview = new TextView(getContext());
        errorTextview.setTextColor(ActivityCompat.getColor(context, R.color.coral_red));
        errorTextview.setTextSize(0, AndroidUtilities.dp(14));
        errorTextview.setGravity(Gravity.CENTER);
        addView(errorTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 10, 20, 10));
        errorTextview.setVisibility(GONE);

        progressBar = new ProgressBar(context);
        addView(progressBar);
    }

    public void showError(String msg) {
        if (progressBar != null) progressBar.setVisibility(GONE);
        if (errorTextview == null) return;
        errorTextview.setText(msg);
        errorTextview.setVisibility(VISIBLE);
    }

    public void update(int type, String txHash, String from, String to, String denom, String amount, String fee, String memo, String myAddress) {
        if (progressBar != null) progressBar.setVisibility(GONE);

        View transactionDetailView = null;
        switch (type) {
            case TxConfirmView.TYPE_SEND:
                transactionDetailView = new TransactionSendDetailView(getContext(), TransactionSendDetailView.TYPE_CONFIRM, false, true, txHash, from, to, denom, amount, fee, memo, myAddress);
                break;
            case TxConfirmView.TYPE_STAKE:
                transactionDetailView = new TransactionStakeDetailView(getContext(), TransactionStakeDetailView.TYPE_CONFIRM, true, txHash, to, denom, amount, fee);
                break;
            case TxConfirmView.TYPE_UNSTAKE:
                transactionDetailView = new TransactionStakeDetailView(getContext(), TransactionStakeDetailView.TYPE_CONFIRM, true, txHash, to, denom, amount, fee);
                break;
            case TxConfirmView.TYPE_REDELEGATE:
                transactionDetailView = new TransactionSendDetailView(getContext(), TransactionSendDetailView.TYPE_CONFIRM, false, true, txHash, from, to, denom, amount, fee, memo, myAddress);
                break;
            case TxConfirmView.TYPE_VOTE:
                transactionDetailView = new TransactionVoteDetailView(getContext(), TransactionVoteDetailView.TYPE_CONFIRM, true, txHash, to, amount, fee);
                break;
            case TxConfirmView.TYPE_CLAIM_REWARDS:
                transactionDetailView = new TransactionClaimRewardDetailView(getContext(), TransactionClaimRewardDetailView.TYPE_CONFIRM, true, txHash, amount, fee);
                break;
        }

        if (transactionDetailView != null)
            addView(transactionDetailView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }
}
