package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.GovernanceActivity;
import com.lunamint.lunagram.ui.SettingsActivity;
import com.lunamint.lunagram.ui.StakingActivity;
import com.lunamint.lunagram.ui.TransactionHistoryActivity;
import com.lunamint.lunagram.ui.ValidatorListActivity;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class StakeToolsView extends CardView {

    private static final int BUTTON_STAKE = 0;
    private static final int BUTTON_UNSTAKE = 1;
    private static final int BUTTON_REDELEGATE = 2;

    private int toolIcons[] = {R.drawable.ic_stake_tools_stake, R.drawable.ic_stake_tools_unstake, R.drawable.ic_stake_tools_redelegate};

    private String accountName;
    private String address;

    private ImageView[] toolIconImageviews;
    private TextView[] toolTextviews;

    public StakeToolsView(Context context, String accountName, String address) {
        super(context);

        this.accountName = accountName;
        this.address = address;

        setBackgroundResource(R.drawable.bg_card_white_radius12);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        addView(mainLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        String toolNames[] = {LocaleController.getString("stake", R.string.stake), LocaleController.getString("unstake", R.string.unstake), LocaleController.getString("redelegate", R.string.redelegate)};
        toolIconImageviews = new ImageView[toolNames.length];
        toolTextviews = new TextView[toolNames.length];
        for (int i = 0; toolNames.length > i; i++) {
            LinearLayout toolButton = new LinearLayout(context);
            toolButton.setOrientation(LinearLayout.VERTICAL);
            toolButton.setGravity(Gravity.CENTER_HORIZONTAL);
            toolButton.setId(i);
            toolButton.setOnClickListener(onClickToolListener);
            mainLayout.addView(toolButton, LayoutHelper.createLinear(60, LayoutHelper.WRAP_CONTENT));

            toolIconImageviews[i] = new ImageView(context);
            toolIconImageviews[i].setScaleType(ImageView.ScaleType.FIT_XY);
            toolIconImageviews[i].setImageResource(toolIcons[i]);
            toolButton.addView(toolIconImageviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 6));

            toolTextviews[i] = new TextView(context);
            toolTextviews[i].setText(toolNames[i]);
            toolTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
            toolTextviews[i].setTextSize(0, AndroidUtilities.dp(12));
            toolTextviews[i].setGravity(Gravity.CENTER);
            toolButton.addView(toolTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            if (i < toolNames.length - 1) {
                LinearLayout space = new LinearLayout(context);
                space.setGravity(Gravity.CENTER);
                mainLayout.addView(space, LayoutHelper.createLinear(0, 48, 1.0f));

                ImageView line = new ImageView(context);
                line.setBackgroundColor(ActivityCompat.getColor(context, R.color.languid_lavender));
                space.addView(line, LayoutHelper.createLinear(1, LayoutHelper.MATCH_PARENT));
            }
        }
    }

    private OnClickListener onClickToolListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case StakeToolsView.BUTTON_STAKE:
                    showValidatorListActivity(ValidatorListActivity.TYPE_STAKE);
                    break;
                case StakeToolsView.BUTTON_UNSTAKE:
                    showValidatorListActivity(ValidatorListActivity.TYPE_UNSTAKE);
                    break;
                case StakeToolsView.BUTTON_REDELEGATE:
                    showValidatorListActivity(ValidatorListActivity.TYPE_REDELEGATE);
                    break;
                default:
            }
        }
    };

    private void showValidatorListActivity(int type) {
        Intent intent = new Intent(getContext(), ValidatorListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        getContext().startActivity(intent);
    }
}
