package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.model.Validator;
import com.lunamint.wallet.utils.NumberFormatter;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ValidatorListCell extends LinearLayout {

    private TextView powerTextview;
    private TextView nameTextview;
    private TextView commissionTextview;
    private TextView stakeAmountTextview;

    public ValidatorListCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundColor(ActivityCompat.getColor(context, R.color.white));
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));

        ImageView profileImageview = new ImageView(context);
        profileImageview.setImageResource(R.drawable.validator_profile_small);
        addView(profileImageview, LayoutHelper.createLinear(40, 40, 0, 0, 10, 0));

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(VERTICAL);
        addView(textLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 10, 0));

        powerTextview = new TextView(context);
        powerTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
        powerTextview.setTextSize(0, AndroidUtilities.dp(10));
        textLayout.addView(powerTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        nameTextview = new TextView(context);

        nameTextview.setTextSize(0, AndroidUtilities.dp(16));
        textLayout.addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        commissionTextview = new TextView(context);
        commissionTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        commissionTextview.setTextSize(0, AndroidUtilities.dp(12));
        textLayout.addView(commissionTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        stakeAmountTextview = new TextView(context);
        stakeAmountTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        stakeAmountTextview.setTextSize(0, AndroidUtilities.dp(16));
        addView(stakeAmountTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 12, 0));
    }


    public void update(Validator data, double totalPower) {
        try {
            if (data.getUnstakingCompletionTime() == null) {
                double power = data.getDelegatorShares();
                if (totalPower > 0) {
                    power = power / totalPower * 100;
                    powerTextview.setText("#" + data.getRank() + " (" + String.format("%.2f", power) + "%)");
                } else {
                    powerTextview.setText("#" + data.getRank() + " (" + String.format("%.2f", data.getDelegatorShares()) + ")");
                }
            } else {
                powerTextview.setText(LocaleController.getString("completionTime", R.string.completionTime) + " (UTC)\n" + data.getUnstakingCompletionTime());
            }

            float commissionRate = Float.parseFloat(data.getCommission().getRate()) * 100;
            commissionTextview.setText(LocaleController.getString("commission", R.string.commission) + " - " + String.format("%.1f", commissionRate) + "%");
        } catch (Exception e) {
            powerTextview.setText("#" + data.getRank() + " (" + String.format("%.2f", data.getDelegatorShares()) + ")");
            commissionTextview.setText(LocaleController.getString("commission", R.string.commission) + " - " + String.format("%.1f", data.getCommission().getRate()) + "");
        }

        if (data.getJailed()) {
            nameTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.coral_red));
        } else {
            nameTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
        }
        nameTextview.setText(data.getDescription().getMoniker());

        if (data.getDelegatedAmount() > 0) {
            stakeAmountTextview.setText(NumberFormatter.getNumber(data.getDelegatedAmountForDisplay()) + " " + Blockchain.getInstance().getReserveDisplayName());
        } else {
            stakeAmountTextview.setText("");
        }
    }
}
