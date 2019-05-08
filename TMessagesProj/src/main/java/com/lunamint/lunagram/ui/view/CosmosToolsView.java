package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
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
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class CosmosToolsView extends CardView {

    private static final int BUTTON_STAKE = 0;
    private static final int BUTTON_VOTE = 1;
    private static final int BUTTON_HISTORY = 2;
    private static final int BUTTON_SETTINGS = 3;

    private boolean isEnabled = false;

    private int toolIcons[] = {R.drawable.ic_cosmos_tools_stake, R.drawable.ic_cosmos_tools_vote, R.drawable.ic_cosmos_tools_history, R.drawable.ic_cosmos_tools_setting};
    private int toolDisabledIcons[] = {R.drawable.ic_cosmos_tools_stake_disabled, R.drawable.ic_cosmos_tools_vote_disabled, R.drawable.ic_cosmos_tools_history_disabled, R.drawable.ic_cosmos_tools_setting_disabled};

    private AccountInfo accountInfo;

    private ImageView[] toolIconImageviews;
    private TextView[] toolTextviews;

    public CosmosToolsView(Context context) {
        super(context);

        setBackgroundResource(R.drawable.bg_card_white_radius12);
        setUseCompatPadding(true);
        setRadius(AndroidUtilities.dp(12));
        setCardElevation(UiUtil.getDefaultElevation());

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        addView(mainLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        String toolNames[] = {LocaleController.getString("stake", R.string.stake), LocaleController.getString("vote", R.string.vote), LocaleController.getString("history", R.string.history), LocaleController.getString("settings", R.string.settings)};
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
            toolIconImageviews[i].setImageResource(toolDisabledIcons[i]);
            toolButton.addView(toolIconImageviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            toolTextviews[i] = new TextView(context);
            toolTextviews[i].setText(toolNames[i]);
            toolTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.manatee));
            toolTextviews[i].setTextSize(0, AndroidUtilities.dp(14));
            toolTextviews[i].setGravity(Gravity.CENTER);
            toolButton.addView(toolTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            if (i < toolNames.length - 1) {
                LinearLayout space = new LinearLayout(context);
                mainLayout.addView(space, LayoutHelper.createLinear(0, 48, 1.0f));
            }
        }
    }

    private View.OnClickListener onClickToolListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case CosmosToolsView.BUTTON_STAKE:
                    if (!isEnabled) return;
                    showStakingActivity();
                    break;
                case CosmosToolsView.BUTTON_VOTE:
                    if (!isEnabled) return;
                    showGovernanceActivity();
                    break;
                case CosmosToolsView.BUTTON_HISTORY:
                    if (!isEnabled) return;
                    showTransactionHistoryActivity();
                    break;
                case CosmosToolsView.BUTTON_SETTINGS:
                    if (accountInfo != null) showSettingsActivity();
                    break;
                default:
            }
        }
    };

    private void showStakingActivity() {
        if (accountInfo == null || accountInfo.getAddress() == null) {
            Toast.makeText(getContext(), LocaleController.getString("noSearchAddressAlert", R.string.noSearchAddressAlert), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), StakingActivity.class);
            intent.putExtra("accountName", accountInfo.getName());
            intent.putExtra("address", accountInfo.getAddress());
            getContext().startActivity(intent);
        }
    }

    private void showGovernanceActivity() {
        Intent intent = new Intent(getContext(), GovernanceActivity.class);
        intent.putExtra("accountName", accountInfo.getName());
        intent.putExtra("address", accountInfo.getAddress());
        getContext().startActivity(intent);
    }

    private void showSettingsActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        intent.putExtra("accountName", accountInfo.getName());
        getContext().startActivity(intent);
    }

    private void showTransactionHistoryActivity() {
        if (accountInfo == null || accountInfo.getAddress() == null) {
            Toast.makeText(getContext(), LocaleController.getString("noSearchAddressAlert", R.string.noSearchAddressAlert), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
            intent.putExtra("address", accountInfo.getAddress());
            intent.putExtra("startType", TransactionHistoryActivity.TYPE_TRANSFER);
            getContext().startActivity(intent);
        }
    }

    public void setEnable(boolean isEnabled, AccountInfo accountInfo) {
        this.isEnabled = isEnabled;
        this.accountInfo = accountInfo;
        update();
    }

    private void update() {
        if (toolIconImageviews == null || toolIconImageviews.length == 0 || toolIconImageviews[0] == null)
            return;
        for (int i = 0; toolIconImageviews.length > i; i++) {
            if (isEnabled) {
                toolIconImageviews[i].setImageResource(toolIcons[i]);
                toolTextviews[i].setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));
            } else {
                if (accountInfo != null && i == 3) {
                    toolIconImageviews[i].setImageResource(toolIcons[i]);
                    toolTextviews[i].setTextColor(ActivityCompat.getColor(getContext(), R.color.charcoal));

                } else {
                    toolIconImageviews[i].setImageResource(toolDisabledIcons[i]);
                    toolTextviews[i].setTextColor(ActivityCompat.getColor(getContext(), R.color.manatee));
                }

            }
        }
    }
}
