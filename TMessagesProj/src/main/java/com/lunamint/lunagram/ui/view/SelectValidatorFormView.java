package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.StakeCoinActivity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class SelectValidatorFormView extends LinearLayout {

    private SelectValidatorCardView selectValidatorCardView;
    private SelectValidatorCardView selectRedelegateValidatorCardView;

    public SelectValidatorFormView(Context context, int type, String validator, OnClickListener onClickListener) {
        super(context);

        String title = "";
        String desc = "";

        switch (type) {
            case StakeCoinActivity.TYPE_STAKE:
                title = LocaleController.getString("confirmValidator", R.string.confirmValidator);
                desc = LocaleController.getString("chooseValidatorStake", R.string.chooseValidatorStake);
                break;
            case StakeCoinActivity.TYPE_UNSTAKE:
                title = LocaleController.getString("confirmValidator", R.string.confirmValidator);
                desc = LocaleController.getString("chooseValidatorUnstake", R.string.chooseValidatorUnstake);
                break;
            case StakeCoinActivity.TYPE_REDELEGATE:
                title = LocaleController.getString("selectValidator", R.string.selectValidator);
                desc = LocaleController.getString("chooseValidatorRestake", R.string.chooseValidatorRestake);
                break;
        }

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setText(title);
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(desc);
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 40));

        if (type == StakeCoinActivity.TYPE_REDELEGATE) {
            selectValidatorCardView = new SelectValidatorCardView(context, validator, null);
            addView(selectValidatorCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 28));

            ImageView arrowImageView = new ImageView(context);
            arrowImageView.setImageResource(R.drawable.arrow_down_black);
            addView(arrowImageView, LayoutHelper.createLinear(22, 13, 0, 0, 0, 28));

            selectRedelegateValidatorCardView = new SelectValidatorCardView(context, null, onClickListener);
            addView(selectRedelegateValidatorCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 40));
        } else {
            selectValidatorCardView = new SelectValidatorCardView(context, validator, null);
            addView(selectValidatorCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));
        }
    }

    public void updateValidator(String name) {
        if (selectValidatorCardView != null) selectValidatorCardView.update(name);
    }

    public void updateRedelegateValidator(String name) {
        if (selectRedelegateValidatorCardView != null)
            selectRedelegateValidatorCardView.update(name);
    }
}
