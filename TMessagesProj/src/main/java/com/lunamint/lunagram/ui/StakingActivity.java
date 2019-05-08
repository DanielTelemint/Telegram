package com.lunamint.lunagram.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.ClaimRewardCardView;
import com.lunamint.lunagram.ui.view.StakeToolsView;
import com.lunamint.lunagram.ui.view.StakingStatusCardView;
import com.lunamint.lunagram.ui.view.StakingToolsCardView;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.StakingInfo;
import com.lunamint.wallet.model.Unbonding;
import com.lunamint.wallet.model.UnbondingEntries;
import com.lunamint.wallet.utils.NetworkUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StakingActivity extends LunagramBaseActivity {

    private static int GET_REWARD_DELAY = 20000;

    private boolean isLoadingRewards = false;
    private boolean isRecursive = false;

    private String address;
    private String accountName;

    private Coin reward;

    private StakingStatusCardView stakingStatusCardView;
    private ClaimRewardCardView claimRewardCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("staking", R.string.staking));

        accountName = getIntent().getStringExtra("accountName");
        address = getIntent().getStringExtra("address");

        RelativeLayout mainLayout = new RelativeLayout(this);
        mainLayout.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        ScrollView scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout contentsLayout = new LinearLayout(this);
        contentsLayout.setOrientation(LinearLayout.VERTICAL);
        contentsLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        contentsLayout.setClipToPadding(false);
        contentsLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        stakingStatusCardView = new StakingStatusCardView(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case 0:
                        showValidatorListActivity(ValidatorListActivity.TYPE_VIEW_MY_VALIDATORS);
                        break;
                    case 1:
                        showValidatorListActivity(ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS);
                        break;
                }
            }
        });
        contentsLayout.addView(stakingStatusCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        claimRewardCardView = new ClaimRewardCardView(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reward == null) {
                    Toast.makeText(StakingActivity.this, LocaleController.getString("notEnoughClaimReward", R.string.notEnoughClaimReward), Toast.LENGTH_LONG).show();
                } else {
                    BigDecimal rw = new BigDecimal(reward.getAmount());
                    if (rw.compareTo(BigDecimal.ZERO) == 0) {
                        Toast.makeText(StakingActivity.this, LocaleController.getString("notEnoughClaimReward", R.string.notEnoughClaimReward), Toast.LENGTH_LONG).show();
                    } else {
                        showClaimRewardActivity();
                    }
                }
            }
        });
        contentsLayout.addView(claimRewardCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        StakeToolsView stakeToolsView = new StakeToolsView(this, accountName, address);
        contentsLayout.addView(stakeToolsView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        StakingToolsCardView stakingToolsCardView = new StakingToolsCardView(this, onClickListener);
        contentsLayout.addView(stakingToolsCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 80));

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStakingInfo();
        getUnbondingInfo();
    }

    @Override
    protected void onDestroy() {
        claimRewardCardView = null;
        super.onDestroy();
    }

    private void getStakingInfo() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getDelegations(address).enqueue(new Callback<ArrayList<StakingInfo>>() {
            @Override
            public void onResponse(Call<ArrayList<StakingInfo>> call, Response<ArrayList<StakingInfo>> response) {
                if (response.code() == 200) {
                    ArrayList<StakingInfo> stakingInfos = response.body();
                    update(stakingInfos);
                } else if (response.code() == 400) {
                    update(null);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            if (response.errorBody().string().contains("no delegation for this")) {
                                update(null);
                            } else {
                                Toast.makeText(StakingActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(StakingActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(StakingActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<StakingInfo>> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(StakingActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(StakingActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getUnbondingInfo() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getUnbondingDelegations(address).enqueue(new Callback<ArrayList<Unbonding>>() {
            @Override
            public void onResponse(Call<ArrayList<Unbonding>> call, Response<ArrayList<Unbonding>> response) {
                if (response.code() == 200) {
                    ArrayList<Unbonding> unbondingInfos = response.body();
                    updateUnbonding(unbondingInfos);
                } else if (response.code() == 400) {
                    updateUnbonding(null);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            if (response.errorBody().string().contains("no delegation for this")) {
                                updateUnbonding(null);
                            } else {
                                Toast.makeText(StakingActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(StakingActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(StakingActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Unbonding>> call, Throwable t) {
            }
        });
    }

    private void getRewards() {
        if (isLoadingRewards || claimRewardCardView == null) return;
        isLoadingRewards = true;
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getRewards(address).enqueue(new Callback<ArrayList<Coin>>() {
            @Override
            public void onResponse(Call<ArrayList<Coin>> call, Response<ArrayList<Coin>> response) {
                updateClaimReward(response.body(), true);
                isLoadingRewards = false;
            }

            @Override
            public void onFailure(Call<ArrayList<Coin>> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(StakingActivity.this)) {
                    showNodeConnectionErrorAlert();
                }
                isLoadingRewards = false;
            }
        });
    }

    private void update(ArrayList<StakingInfo> stakingInfos) {
        double stakingAmount = 0;

        if (stakingInfos != null) {
            for (int i = 0; stakingInfos.size() > i; i++) {
                stakingAmount = stakingAmount + stakingInfos.get(i).getShares();
            }
        }

        Coin coin = new Coin();
        coin.setAmount(stakingAmount + "");
        coin.setDenom(Blockchain.getInstance().getReserveDenom());

        stakingStatusCardView.updateStaking(coin);

        if (!isRecursive && stakingAmount > 0) {
            isRecursive = true;
            getRewards();
        } else {
            if (!isRecursive) updateClaimReward(null, false);
        }
    }

    private void updateUnbonding(ArrayList<Unbonding> unbondingInfos) {
        BigDecimal unbondingAmount = null;

        if (unbondingInfos != null) {
            for (Unbonding unbonding : unbondingInfos) {
                for (UnbondingEntries entries : unbonding.getEntries()) {
                    if (unbondingAmount == null) unbondingAmount = new BigDecimal("0");
                    unbondingAmount = unbondingAmount.add(entries.getBalance());
                }
            }
        }

        Coin coin = new Coin();
        if (unbondingAmount != null) {
            coin.setAmount(unbondingAmount.toString());
        } else {
            coin.setAmount("0");
        }

        coin.setDenom(Blockchain.getInstance().getReserveDenom());
        stakingStatusCardView.updateUnstaking(coin);
    }

    private void delayGetRewards() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                getRewards();
            }
        }, GET_REWARD_DELAY);
    }

    private void updateClaimReward(ArrayList<Coin> response, boolean isRecursive) {
        if (claimRewardCardView == null || isFinishing()) return;

        BigDecimal amount = new BigDecimal("0");
        if (response != null) {
            for (Coin coin : response) {
                if (coin.getDenom().equals(Blockchain.getInstance().getReserveDenom()))
                    amount = amount.add(new BigDecimal(coin.getAmountOrigin()));
            }
        }

        reward = new Coin();
        reward.setAmount(amount.toString());
        reward.setDenom(Blockchain.getInstance().getReserveDenom());

        claimRewardCardView.update(reward);

        if (isRecursive) delayGetRewards();
    }

    private void showValidatorListActivity(int type) {
        Intent intent = new Intent(StakingActivity.this, ValidatorListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    private void showTransactionHistoryActivity() {
        Intent intent = new Intent(StakingActivity.this, TransactionHistoryActivity.class);
        intent.putExtra("startType", TransactionHistoryActivity.TYPE_STAKING);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    private void showClaimRewardActivity() {
        Intent intent = new Intent(StakingActivity.this, ClaimRewardActivity.class);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        intent.putExtra("rewards", reward.getAmount());
        startActivity(intent);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 0:
                    showValidatorListActivity(ValidatorListActivity.TYPE_VIEW_MY_VALIDATORS);
                    break;
                case 1:
                    showValidatorListActivity(ValidatorListActivity.TYPE_VIEW_VALIDATORS);
                    break;
                case 2:
                    showTransactionHistoryActivity();
                    break;
            }
        }
    };
}
