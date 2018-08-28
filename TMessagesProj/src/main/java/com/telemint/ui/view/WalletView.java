package com.telemint.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telemint.messenger.R;
import com.telemint.ui.model.AccountInfo;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class WalletView extends RelativeLayout {

    private static final int STAKE_BUTTON = 0;
    private static final int SEND_BUTTON = 1;

    private static final int STAKING_BUTTON = 0;
    private static final int GOVERNANCE_BUTTON = 1;
    private static final int COSMOS_EXPLORER_BUTTON = 2;
    private static final int TRANSACTION_HISTORY_BUTTON = 3;
    private static final int NOTIFICATION_BUTTON = 4;

    private LinearLayout emptyAccountLayout;

    private LinearLayout accountLayout;
    private TextView accountNameTextview;
    private TextView addressTextview;
    private TextView dailyRewardTextview;
    private TextView balanceTextview;
    private TextView stakedTextview;


    public void update(AccountInfo data){
        if(data == null){
            showEmptyAccountLayout();
            return;
        } else {
            showAccountLayout();
        }

        accountNameTextview.setText(data.getAccountName());
        addressTextview.setText(data.getAddress());
        dailyRewardTextview.setText(data.getDailyReward() + " " + data.getDenom());
        balanceTextview.setText(data.getBalance() + " " + data.getDenom());
        stakedTextview.setText(getContext().getString(R.string.staked)+": "+data.getStakedBalance() + " " + data.getDenom());

    }

    public WalletView(Context context) {
        super(context);

        setBackground(context.getResources().getDrawable(R.drawable.wallet_tab_bg));
        FrameLayout cardLayout = new FrameLayout(context);

        addView(cardLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,18,47,18,0));


        emptyAccountLayout = new LinearLayout(context);
        emptyAccountLayout.setOrientation(LinearLayout.VERTICAL);
        emptyAccountLayout.setGravity(Gravity.CENTER);
        emptyAccountLayout.setBackgroundResource(R.drawable.wallet_tab_empty_card_bg);
        cardLayout.addView(emptyAccountLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        emptyAccountLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyAccountLayout.setVisibility(INVISIBLE);
                accountLayout.setVisibility(VISIBLE);
                showCreateAccountActivity();
            }
        });
        //emptyAccountLayout.setVisibility(INVISIBLE);


        ImageView plusIconImageview = new ImageView(context);
        plusIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        plusIconImageview.setImageResource(R.drawable.icon_plus);
        emptyAccountLayout.addView(plusIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,0,0,0,6));

        accountNameTextview = new TextView(context);
        accountNameTextview.setText(getContext().getText(R.string.createNewAccount));
        accountNameTextview.setTextColor(getResources().getColor(R.color.text_value_white));
        accountNameTextview.setTextSize(0, AndroidUtilities.dp(12));
        emptyAccountLayout.addView(accountNameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT));

        accountLayout = new LinearLayout(context);
        accountLayout.setOrientation(LinearLayout.VERTICAL);
        accountLayout.setGravity(Gravity.CENTER);
        accountLayout.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        accountLayout.setBackgroundResource(R.drawable.wallet_tab_card_bg);
        cardLayout.addView(accountLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        accountLayout.setVisibility(INVISIBLE);


        LinearLayout cardHeaderLayout = new LinearLayout(context);
        cardHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardHeaderLayout.setGravity(Gravity.CENTER_VERTICAL);
        accountLayout.addView(cardHeaderLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        accountNameTextview = new TextView(context);
        accountNameTextview.setText("Cosmos Wallet 1");
        accountNameTextview.setTextColor(getResources().getColor(R.color.text_title_default));
        accountNameTextview.setTextSize(0, AndroidUtilities.dp(18));
        cardHeaderLayout.addView(accountNameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,10,0));


        Button copyButton = new Button(context);
        copyButton.setBackgroundResource(R.drawable.btn_copy);
        cardHeaderLayout.addView(copyButton, LayoutHelper.createLinear(22,22,0,0,6,0));

        Button qrCodeButton = new Button(context);
        qrCodeButton.setBackgroundResource(R.drawable.btn_qr_code);
        cardHeaderLayout.addView(qrCodeButton, LayoutHelper.createLinear(22,22));


        addressTextview = new TextView(context);
        addressTextview.setText("cosmosaccaddr1rvm0em6w3qkzcwnzf9hkqvksujl895dfww4ecn");
        addressTextview.setTextColor(getResources().getColor(R.color.text_value_grey));
        addressTextview.setTextSize(0, AndroidUtilities.dp(9));
        accountLayout.addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT,0,0,0,15));

        LinearLayout dailyRewardLayout = new LinearLayout(context);
        dailyRewardLayout.setOrientation(LinearLayout.HORIZONTAL);
        dailyRewardLayout.setGravity(Gravity.CENTER);
        dailyRewardLayout.setBackgroundResource(R.drawable.stake_info_bg);
        dailyRewardLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(4), AndroidUtilities.dp(10), AndroidUtilities.dp(4));
        accountLayout.addView(dailyRewardLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,0,10));

        TextView rewardTitleTextview = new TextView(context);
        rewardTitleTextview.setText("24hr");
        rewardTitleTextview.setTextColor(getResources().getColor(R.color.text_title_default));
        rewardTitleTextview.setTextSize(0, AndroidUtilities.dp(10));
        dailyRewardLayout.addView(rewardTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT));

        ImageView rewardArrowImageview = new ImageView(context);
        rewardArrowImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        rewardArrowImageview.setImageResource(R.drawable.reward_arrow_up);
        dailyRewardLayout.addView(rewardArrowImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,6,0));

        dailyRewardTextview = new TextView(context);
        dailyRewardTextview.setText("285.25 Atom");
        dailyRewardTextview.setTextColor(getResources().getColor(R.color.text_title_default));
        dailyRewardTextview.setTextSize(0, AndroidUtilities.dp(10));
        dailyRewardLayout.addView(dailyRewardTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT));

        balanceTextview = new TextView(context);
        balanceTextview.setText("8,043,914.55 Atom");
        balanceTextview.setTextColor(getResources().getColor(R.color.text_value_default));
        balanceTextview.setTextSize(0, AndroidUtilities.dp(22));
        accountLayout.addView(balanceTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,0,5));

        stakedTextview = new TextView(context);
        stakedTextview.setText("Staked: 4,000,841.53 Atom");
        stakedTextview.setTextColor(getResources().getColor(R.color.text_value_default));
        stakedTextview.setTextSize(0, AndroidUtilities.dp(12));
        accountLayout.addView(stakedTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,0,17));

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        accountLayout.addView(buttonLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT));

        Button stakeButton = new Button(context);
        stakeButton.setId(WalletView.STAKE_BUTTON);
        stakeButton.setGravity(Gravity.CENTER);
        stakeButton.setPadding(AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10), 0);
        stakeButton.setOnClickListener(onClickAccountListener);
        stakeButton.setBackgroundResource(R.drawable.btn_black);
        stakeButton.setTextSize(0, AndroidUtilities.dp(14));
        stakeButton.setText(context.getString(R.string.stake));
        buttonLayout.addView(stakeButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,30,0,0,10,0));

        Button sendButton = new Button(context);
        sendButton.setId(WalletView.SEND_BUTTON);
        sendButton.setGravity(Gravity.CENTER);
        sendButton.setPadding(AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10), 0);
        sendButton.setOnClickListener(onClickAccountListener);
        sendButton.setBackgroundResource(R.drawable.btn_black);
        sendButton.setTextSize(0, AndroidUtilities.dp(14));
        sendButton.setText(context.getString(R.string.send));
        buttonLayout.addView(sendButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,30));

        LinearLayout toolsLayout = new LinearLayout(context);
        toolsLayout.setOrientation(LinearLayout.VERTICAL);
        toolsLayout.setGravity(Gravity.CENTER);
        toolsLayout.setBackgroundResource(R.drawable.cosmos_tools_bg);
        addView(toolsLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, ALIGN_PARENT_BOTTOM));


        TextView toolsTitleTextview = new TextView(context);
        toolsTitleTextview.setText(context.getString(R.string.cosmosTools));
        toolsTitleTextview.setTextColor(getResources().getColor(R.color.text_title_default));
        toolsTitleTextview.setTextSize(0, AndroidUtilities.dp(24));
        toolsTitleTextview.setTypeface(null, Typeface.BOLD);
        toolsLayout.addView(toolsTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,0,20));

        LinearLayout toolsButtonLayout = new LinearLayout(context);
        toolsButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        toolsButtonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        toolsLayout.addView(toolsButtonLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,10,0,0,0));

        int toolIcons[] = {R.drawable.btn_cosmos_tools_staking, R.drawable.btn_cosmos_tools_governance, R.drawable.btn_cosmos_tools_explorer, R.drawable.btn_cosmos_tools_tx_history, R.drawable.btn_cosmos_tools_noti};
        String toolNames[] = {context.getString(R.string.staking), context.getString(R.string.governance), context.getString(R.string.cosmosExplorer), context.getString(R.string.transactionHistory), context.getString(R.string.notifications)};
        for(int i = 0; toolNames.length > i; i++){
            LinearLayout toolButton = new LinearLayout(context);
            toolButton.setOrientation(LinearLayout.VERTICAL);
            toolButton.setGravity(Gravity.CENTER_HORIZONTAL);
            toolButton.setId(i);
            toolButton.setOnClickListener(onClickToolListener);
            toolsButtonLayout.addView(toolButton, LayoutHelper.createLinear(54, LayoutHelper.WRAP_CONTENT,0,0,10,0));

            ImageView toolIconImageview = new ImageView(context);
            toolIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
            toolIconImageview.setImageResource(toolIcons[i]);
            toolButton.addView(toolIconImageview, LayoutHelper.createLinear(44,44,0,0,0,4));

            TextView toolTextview = new TextView(context);
            toolTextview.setText(toolNames[i]);
            toolTextview.setTextColor(getResources().getColor(R.color.text_value_default));
            toolTextview.setTextSize(0, AndroidUtilities.dp(10));
            toolTextview.setGravity(Gravity.CENTER);
            toolButton.addView(toolTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT));
        }



    }

    private void showEmptyAccountLayout(){
        emptyAccountLayout.setVisibility(VISIBLE);
        accountLayout.setVisibility(INVISIBLE);
    }

    private void showAccountLayout(){
        emptyAccountLayout.setVisibility(INVISIBLE);
        accountLayout.setVisibility(VISIBLE);
    }

    private View.OnClickListener onClickAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case WalletView.STAKE_BUTTON:
                    showValidatorActivity();
                    break;
                case WalletView.SEND_BUTTON:
                    showSendActivity();
                    break;
            }
        }
    };

    private View.OnClickListener onClickToolListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case WalletView.STAKING_BUTTON:
                    showStakingActivity();
                    break;
                case WalletView.GOVERNANCE_BUTTON:
                    showGovernanceActivity();
                    break;
                case WalletView.COSMOS_EXPLORER_BUTTON:
                    showCosmosExplorerActivity();
                    break;
                case WalletView.TRANSACTION_HISTORY_BUTTON:
                    showTransactionHistoryActivity();
                    break;
                case WalletView.NOTIFICATION_BUTTON:
                    showNotificationActivity();
                    break;
                default:
            }
        }
    };

    private void showCreateAccountActivity(){
        // TODO. start CreateAccountActivity
    }

    private void showValidatorActivity(){
        // TODO. start ValidatorActivity
    }

    private void showSendActivity(){
        // TODO. start ValidatorActivity
    }

    private void showStakingActivity(){
        // TODO. start StakingActivity
    }

    private void showGovernanceActivity(){
        // TODO. start GovernanceActivity
    }

    private void showCosmosExplorerActivity(){
        // TODO. start CosmosExplorerActivity
    }

    private void showTransactionHistoryActivity(){
        // TODO. start TransactionHistoryActivity
    }

    private void showNotificationActivity(){
        // TODO. start WalletNotificationActivity
    }




}
