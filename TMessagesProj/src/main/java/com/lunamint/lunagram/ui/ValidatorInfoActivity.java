package com.lunamint.lunagram.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.StakingStatusCardView;
import com.lunamint.lunagram.ui.view.ValidatorProfileView;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.StakingInfo;
import com.lunamint.wallet.model.Unbonding;
import com.lunamint.wallet.model.UnbondingEntries;
import com.lunamint.wallet.model.Validator;
import com.lunamint.wallet.utils.NetworkUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.LayoutHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValidatorInfoActivity extends LunagramBaseActivity {

    private String accountName;
    private String address;
    private double totalPower;
    private Validator validatorInfo;

    private ValidatorProfileView validatorProfileView;
    private StakingStatusCardView myStakingInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("validator", R.string.validator));

        accountName = getIntent().getStringExtra("accountName");
        address = getIntent().getStringExtra("address");
        totalPower = getIntent().getDoubleExtra("totalPower", 0);
        validatorInfo = (Validator) getIntent().getSerializableExtra("validatorInfo");

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        ScrollView scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        LinearLayout contentsLayout = new LinearLayout(this);
        contentsLayout.setOrientation(LinearLayout.VERTICAL);
        contentsLayout.setClipToPadding(false);
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        validatorProfileView = new ValidatorProfileView(this, onClickListener);
        myStakingInfoView = new StakingStatusCardView(this, null);

        contentsLayout.addView(validatorProfileView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        contentsLayout.addView(myStakingInfoView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 0, 20, 20));

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setClipToPadding(false);
        buttonLayout.setPadding(AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        mainLayout.addView(buttonLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        DefaultButton unstakeButton = new DefaultButton(this, 4, true, LocaleController.getString("unstake", R.string.unstake), R.drawable.btn_radius4_dark_blue, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDelegateTypeDialog();
            }
        });
        buttonLayout.addView(unstakeButton, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        int stakeButtonBg;
        if (validatorInfo != null && !validatorInfo.getJailed()) {
            stakeButtonBg = R.drawable.btn_radius4_blue;
        } else {
            stakeButtonBg = R.drawable.btn_grey;
        }
        DefaultButton stakeButton = new DefaultButton(this, 4, true, LocaleController.getString("stake", R.string.stake), stakeButtonBg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStakeCoinActivity(StakeCoinActivity.TYPE_STAKE);
            }
        });
        buttonLayout.addView(stakeButton, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        update();
        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStakingInfo();
        getUnbondingInfo();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case ValidatorProfileView.ACTION_SHOW_HISTORY:
                    showTransactionHistoryActivity();
                    break;
                case ValidatorProfileView.ACTION_SHOW_WEBSITE:
                    showWebsite();
                    break;
            }
        }
    };

    private void showSelectDelegateTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new CharSequence[]{LocaleController.getString("unstake", R.string.unstake), LocaleController.getString("redelegate", R.string.redelegate)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    showStakeCoinActivity(StakeCoinActivity.TYPE_UNSTAKE);
                } else {
                    showStakeCoinActivity(StakeCoinActivity.TYPE_REDELEGATE);
                }
            }
        });
        builder.create().show();
    }

    private void showStakeCoinActivity(int type) {
        if (type == StakeCoinActivity.TYPE_STAKE && validatorInfo.getJailed()) {
            Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("jailedValidatorError", R.string.jailedValidatorError), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(ValidatorInfoActivity.this, StakeCoinActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        intent.putExtra("validatorName", validatorInfo.getDescription().getMoniker());
        intent.putExtra("validatorAddress", validatorInfo.getOperatorAddress());
        startActivity(intent);
    }

    private void showTransactionHistoryActivity() {
        Intent intent = new Intent(ValidatorInfoActivity.this, TransactionHistoryActivity.class);
        intent.putExtra("startType", TransactionHistoryActivity.TYPE_STAKING);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    private void showWebsite() {
        if (validatorInfo == null || validatorInfo.getDescription() == null || validatorInfo.getDescription().getWebsite() == null || !validatorInfo.getDescription().getWebsite().contains("http"))
            return;
        String url = validatorInfo.getDescription().getWebsite();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void getStakingInfo() {

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getStakingInfo(address, validatorInfo.getOperatorAddress()).enqueue(new Callback<StakingInfo>() {
            @Override
            public void onResponse(Call<StakingInfo> call, Response<StakingInfo> response) {
                if (response.code() == 200) {
                    StakingInfo data = response.body();
                    if (data != null) {
                        updateMyStakingInfo(data.getShares());
                    } else {
                        updateMyStakingInfo(0);
                    }
                } else if (response.code() == 400) {
                    updateMyStakingInfo(0);
                } else {
                    try {
                        if (response.errorBody() != null && response.errorBody().string().contains("no delegation for this")) {
                            updateMyStakingInfo(0);
                        } else {
                            Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.lunamint.wallet.model.StakingInfo> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(ValidatorInfoActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getUnbondingInfo() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getUnbondingDelegation(address, validatorInfo.getOperatorAddress()).enqueue(new Callback<Unbonding>() {
            @Override
            public void onResponse(Call<Unbonding> call, Response<Unbonding> response) {
                if (response.code() == 200 && response.body() != null) {
                    Unbonding unbondingInfos = response.body();
                    updateUnbonding(unbondingInfos.getEntries());
                } else if (response.code() == 400) {
                    updateUnbonding(null);
                } else {
                    try {
                        if (response.errorBody() != null && response.errorBody().string().contains("no unbonding delegation found")) {
                            updateUnbonding(null);
                        } else {
                            Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Unbonding> call, Throwable t) {
                Toast.makeText(ValidatorInfoActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void update() {
        if (validatorInfo == null) return;
        validatorProfileView.update(validatorInfo, totalPower);
    }

    private void updateMyStakingInfo(double amount) {
        Coin coin = new Coin();
        coin.setAmount(amount + "");
        coin.setDenom(Blockchain.getInstance().getReserveDenom());
        myStakingInfoView.updateStaking(coin);
    }

    private void updateUnbonding(ArrayList<UnbondingEntries> unbondingEntries) {
        BigDecimal amount = null;
        if (unbondingEntries != null) {
            for (UnbondingEntries entries : unbondingEntries) {
                if (amount == null) amount = new BigDecimal("0");
                amount = amount.add(entries.getBalance());
            }
        }

        Coin coin = new Coin();
        if (amount != null) {
            coin.setAmount(amount.toString());
        } else {
            coin.setAmount("0");
        }
        coin.setDenom(Blockchain.getInstance().getReserveDenom());

        myStakingInfoView.updateUnstaking(coin);
    }
}
