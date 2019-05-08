package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.TransactionHistory;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.Parser;
import com.lunamint.wallet.utils.TokenUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TransactionHistoryCell extends LinearLayout {

    private ImageView statusImgview;
    private ImageView profileImgview;
    private TextView blockHeightTextview;
    private TextView typeTextview;
    private TextView amountTextview;
    private TextView addressTextview;

    public TransactionHistoryCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundColor(ActivityCompat.getColor(context, R.color.white));
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));

        FrameLayout imgFramelayout = new FrameLayout(context);
        addView(imgFramelayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 10, 0));

        profileImgview = new ImageView(context);
        imgFramelayout.addView(profileImgview, LayoutHelper.createLinear(42, 42));

        statusImgview = new ImageView(context);
        imgFramelayout.addView(statusImgview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout defaultInfoLayout = new LinearLayout(context);
        defaultInfoLayout.setOrientation(LinearLayout.VERTICAL);
        defaultInfoLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(defaultInfoLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 10, 0));

        blockHeightTextview = new TextView(context);
        blockHeightTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
        blockHeightTextview.setTextSize(0, AndroidUtilities.dp(10));
        defaultInfoLayout.addView(blockHeightTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        typeTextview = new TextView(context);
        typeTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        typeTextview.setTextSize(0, AndroidUtilities.dp(16));
        defaultInfoLayout.addView(typeTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        addressTextview = new TextView(context);
        addressTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        addressTextview.setTextSize(0, AndroidUtilities.dp(12));
        defaultInfoLayout.addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        amountTextview = new TextView(context);
        amountTextview.setTextSize(0, AndroidUtilities.dp(16));
        amountTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        amountTextview.setGravity(Gravity.RIGHT);
        addView(amountTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    public void update(String address, TransactionHistory data) {
        blockHeightTextview.setText("#" + String.valueOf(data.getBlock()));

        switch (data.getType()) {
            case "cosmos-sdk/MsgSend":
                profileImgview.setImageResource(TokenUtil.getTokenIcon(data.getDenom()));
                amountTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
                if (address.equals(data.getFrom())) {
                    statusImgview.setImageResource(R.drawable.ic_history_send);
                    amountTextview.setText("-" + NumberFormatter.getNumber(data.getAmount()) + " " + TokenUtil.getTokenDisplayName(data.getDenom()));
                    addressTextview.setText(Parser.getShotAddressForDisplay(data.getTo()));
                    typeTextview.setText(LocaleController.getString("send", R.string.send));
                } else {
                    statusImgview.setImageResource(R.drawable.ic_history_receive);
                    amountTextview.setText("+" + NumberFormatter.getNumber(data.getAmount()) + " " + TokenUtil.getTokenDisplayName(data.getDenom()));
                    addressTextview.setText(Parser.getShotAddressForDisplay(data.getFrom()));
                    typeTextview.setText(LocaleController.getString("receive", R.string.receive));
                }
                break;
            case "cosmos-sdk/MsgDelegate":
                profileImgview.setImageResource(R.drawable.validator_profile_small);
                statusImgview.setImageResource(R.drawable.ic_history_stake);
                typeTextview.setText(LocaleController.getString("stake", R.string.stake));
                addressTextview.setText(Parser.getShotAddressForDisplay(data.getTo()));

                amountTextview.setText(NumberFormatter.getNumber(data.getAmount()) + " " + Blockchain.getInstance().getReserveDisplayName());
                amountTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
                break;
            case "cosmos-sdk/MsgUndelegate":
                profileImgview.setImageResource(R.drawable.validator_profile_small);
                statusImgview.setImageResource(R.drawable.ic_history_unstake);
                typeTextview.setText(LocaleController.getString("unstake", R.string.unstake));
                addressTextview.setText(Parser.getShotAddressForDisplay(data.getTo()));

                amountTextview.setText(NumberFormatter.getNumber(data.getAmount()) + " " + Blockchain.getInstance().getReserveDisplayName());
                amountTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
                break;
            case "cosmos-sdk/MsgBeginRedelegate":
                profileImgview.setImageResource(R.drawable.validator_profile_small);
                statusImgview.setImageResource(R.drawable.ic_history_restake);
                typeTextview.setText(LocaleController.getString("redelegate", R.string.redelegate));
                addressTextview.setText(Parser.getShotAddressForDisplay(data.getTo()));

                amountTextview.setText(NumberFormatter.getNumber(data.getAmount()) + " " + Blockchain.getInstance().getReserveDisplayName());
                amountTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
                break;
            case "cosmos-sdk/MsgWithdrawDelegationReward":
                profileImgview.setImageResource(TokenUtil.getTokenIcon(Blockchain.getInstance().getReserveDenom()));
                statusImgview.setImageResource(R.drawable.ic_history_reward);
                typeTextview.setText(LocaleController.getString("claimReward", R.string.claimReward));
                amountTextview.setText("");

                break;
            case "cosmos-sdk/MsgVote":
                profileImgview.setImageResource(R.drawable.validator_profile_small);

                switch (data.getAmount()){
                    case "Yes":
                        statusImgview.setImageResource(R.drawable.ic_history_vote_yes);
                        amountTextview.setText(LocaleController.getString("yes", R.string.yes));
                        break;
                    case "No":
                        statusImgview.setImageResource(R.drawable.ic_history_vote_no);
                        amountTextview.setText(LocaleController.getString("no", R.string.no));
                        break;
                    case "NoWithVeto":
                        statusImgview.setImageResource(R.drawable.ic_history_vote_no_with_veto);
                        amountTextview.setText(LocaleController.getString("noWithVeto", R.string.noWithVeto));
                        break;
                    case "Abstain":
                        statusImgview.setImageResource(R.drawable.ic_history_vote_abstain);
                        amountTextview.setText(LocaleController.getString("abstain", R.string.abstain));
                        break;
                }

                typeTextview.setText(LocaleController.getString("proposal", R.string.proposal) + " #" + data.getTo());
                addressTextview.setVisibility(GONE);
                break;
        }
    }
}
