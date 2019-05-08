package com.lunamint.lunagram.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.ValidatorListAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.LcdService;
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
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValidatorListActivity extends LunagramBaseActivity {

    public static final int ACTIVITY_RESULT_OK = 200;

    public static final int TYPE_VIEW_VALIDATORS = 0;
    public static final int TYPE_VIEW_MY_VALIDATORS = 1;
    public static final int TYPE_VIEW_UNSTAKING_VALIDATORS = 2;
    public static final int TYPE_STAKE = 3;
    public static final int TYPE_UNSTAKE = 4;
    public static final int TYPE_REDELEGATE = 5;
    public static final int TYPE_SELECT_VALIDATOR_FROM_ALL = 6;
    public static final int TYPE_SELECT_VALIDATOR_FROM_MY = 7;

    private int type;
    private String accountName;
    private String address;
    private ArrayList<Validator> validatorList;

    private ValidatorListAdapter validatorListAdapter;

    private TextView totalValidatorTextview;

    private LinearLayout contentsLayout;
    private LinearLayout loadingLayout;

    private Comparator<Validator> comparator = new Comparator<Validator>() {
        @Override
        public int compare(Validator o1, Validator o2) {
            return Float.compare(o2.getDelegatorShares(), o1.getDelegatorShares());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getIntExtra("type", 0);
        accountName = getIntent().getStringExtra("accountName");
        address = getIntent().getStringExtra("address");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getActionbarTitle());

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        if (type != ValidatorListActivity.TYPE_STAKE && type != ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_ALL && type != ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_MY) {
            LinearLayout totalValidatorsLayout = new LinearLayout(this);
            totalValidatorsLayout.setOrientation(LinearLayout.HORIZONTAL);
            totalValidatorsLayout.setGravity(Gravity.CENTER_VERTICAL);
            totalValidatorsLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(22), AndroidUtilities.dp(20), AndroidUtilities.dp(12));
            mainLayout.addView(totalValidatorsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView balanceTitleTextview = new TextView(this);
            balanceTitleTextview.setTextSize(0, AndroidUtilities.dp(16));
            balanceTitleTextview.setTextColor(ActivityCompat.getColor(this, R.color.charcoal));
            balanceTitleTextview.setTypeface(Typeface.DEFAULT_BOLD);
            balanceTitleTextview.setText(LocaleController.getString("validators", R.string.validators));
            totalValidatorsLayout.addView(balanceTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 4, 0));

            totalValidatorTextview = new TextView(this);
            totalValidatorTextview.setTextSize(0, AndroidUtilities.dp(16));
            totalValidatorTextview.setTextColor(ActivityCompat.getColor(this, R.color.medium_slate_blue));
            totalValidatorTextview.setTypeface(Typeface.DEFAULT_BOLD);
            totalValidatorsLayout.addView(totalValidatorTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        }

        FrameLayout frameLayout = new FrameLayout(this);
        mainLayout.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        contentsLayout = new LinearLayout(this);
        contentsLayout.setOrientation(LinearLayout.VERTICAL);
        frameLayout.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ListView listView = new ListView(this);
        validatorListAdapter = new ValidatorListAdapter(this, 0, validatorList);
        listView.setAdapter(validatorListAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        contentsLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        contentsLayout.setVisibility(View.INVISIBLE);

        loadingLayout = new LinearLayout(this);
        loadingLayout.setGravity(Gravity.CENTER);
        frameLayout.addView(loadingLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(this);
        loadingLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllValidators();
    }

    private void getAllValidators() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getValidators().enqueue(new Callback<ArrayList<Validator>>() {
            @Override
            public void onResponse(Call<ArrayList<Validator>> call, Response<ArrayList<Validator>> response) {
                if (response.code() == 200) {
                    validatorList = response.body();
                    if (validatorList != null) Collections.sort(validatorList, comparator);
                    if (type != ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS) {
                        getStakingInfo();
                    } else {
                        getUnbondingInfo();
                    }
                } else {
                    update(null, null);
                    String err = "";
                    try {
                        if (response.errorBody() != null) err = response.errorBody().string();
                    } catch (IOException e) {
                        // ignore
                    }

                    Toast.makeText(ValidatorListActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + err, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Validator>> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(ValidatorListActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(ValidatorListActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (validatorList == null) return;
            switch (type) {
                case ValidatorListActivity.TYPE_VIEW_VALIDATORS:
                    showValidatorInfoActivity(validatorList.get(position));
                    break;
                case ValidatorListActivity.TYPE_VIEW_MY_VALIDATORS:
                    showValidatorInfoActivity(validatorList.get(position));
                    break;
                case ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS:
                    break;
                case ValidatorListActivity.TYPE_STAKE:
                    showStakeCoinActivity(validatorList.get(position), StakeCoinActivity.TYPE_STAKE);
                    break;
                case ValidatorListActivity.TYPE_UNSTAKE:
                    showStakeCoinActivity(validatorList.get(position), StakeCoinActivity.TYPE_UNSTAKE);
                    break;
                case ValidatorListActivity.TYPE_REDELEGATE:
                    showStakeCoinActivity(validatorList.get(position), StakeCoinActivity.TYPE_REDELEGATE);
                    break;
                case ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_ALL:
                    finishWithResult(validatorList.get(position));
                    break;
                case ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_MY:
                    finishWithResult(validatorList.get(position));
                    break;
            }
        }
    };

    private void showValidatorInfoActivity(Validator validator) {
        if (validatorListAdapter == null) return;
        Intent intent = new Intent(ValidatorListActivity.this, ValidatorInfoActivity.class);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        intent.putExtra("totalPower", validatorListAdapter.getTotalPower());
        intent.putExtra("validatorInfo", validator);
        startActivity(intent);
    }

    private void showStakeCoinActivity(Validator validator, int type) {
        if (type == StakeCoinActivity.TYPE_STAKE && validator.getJailed()) {
            Toast.makeText(ValidatorListActivity.this, LocaleController.getString("jailedValidatorError", R.string.jailedValidatorError), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(ValidatorListActivity.this, StakeCoinActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        intent.putExtra("validatorName", validator.getDescription().getMoniker());
        intent.putExtra("validatorAddress", validator.getOperatorAddress());
        startActivity(intent);

        if (this.type != ValidatorListActivity.TYPE_VIEW_VALIDATORS && this.type != ValidatorListActivity.TYPE_VIEW_MY_VALIDATORS && this.type != ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS) {
            finish();
        }
    }

    private void getStakingInfo() {
        if (validatorList == null || validatorList.size() == 0) {
            update(null, null);
            return;
        }

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getDelegations(address).enqueue(new Callback<ArrayList<StakingInfo>>() {
            @Override
            public void onResponse(Call<ArrayList<StakingInfo>> call, Response<ArrayList<StakingInfo>> response) {

                if (response.code() == 200) {
                    ArrayList<StakingInfo> data = response.body();
                    update(data, null);
                } else if (response.code() == 400) {
                    update(null, null);
                } else {
                    update(null, null);
                    try {
                        if (response.errorBody() != null && !response.errorBody().string().contains("no delegation for this"))
                            Toast.makeText(ValidatorListActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ValidatorListActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<StakingInfo>> call, Throwable t) {
                update(null, null);
                if (!NetworkUtil.isNetworkAvailable(ValidatorListActivity.this))
                    Toast.makeText(ValidatorListActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUnbondingInfo() {
        if (validatorList == null || validatorList.size() == 0) {
            update(null, null);
            return;
        }

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getUnbondingDelegations(address).enqueue(new Callback<ArrayList<Unbonding>>() {
            @Override
            public void onResponse(Call<ArrayList<Unbonding>> call, Response<ArrayList<Unbonding>> response) {

                if (response.code() == 200) {
                    ArrayList<Unbonding> data = response.body();
                    update(null, data);
                } else if (response.code() == 400) {
                    update(null, null);
                } else {
                    update(null, null);
                    try {
                        if (response.errorBody() != null && !response.errorBody().string().contains("no delegation for this"))
                            Toast.makeText(ValidatorListActivity.this, LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ValidatorListActivity.this, LocaleController.getString("unknownError", R.string.unknownError), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Unbonding>> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(ValidatorListActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(ValidatorListActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void update(ArrayList<StakingInfo> stakingInfos, ArrayList<Unbonding> unbondingInfos) {

        setValidatorRank();
        double totalPower = getTotalPower();

        if (type != ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS) {
            updateValidatorListWithStakingInfos(stakingInfos);
        } else {
            updateValidatorListWithUnbondingInfos(unbondingInfos);
        }

        if (type == ValidatorListActivity.TYPE_UNSTAKE || type == ValidatorListActivity.TYPE_REDELEGATE || type == ValidatorListActivity.TYPE_VIEW_MY_VALIDATORS || type == ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_MY || type == ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS)
            updateMyValidators();

        int validatorCnt = 0;
        if (validatorList != null) validatorCnt = validatorList.size();

        if (totalValidatorTextview != null)
            totalValidatorTextview.setText(String.valueOf(validatorCnt));

        if (validatorListAdapter != null) {
            validatorListAdapter.update(validatorList, totalPower);
            validatorListAdapter.notifyDataSetChanged();
        }

        loadingLayout.setVisibility(View.GONE);
        contentsLayout.setVisibility(View.VISIBLE);

    }

    private void updateValidatorListWithStakingInfos(ArrayList<StakingInfo> stakingInfos) {
        if (stakingInfos != null) {
            for (int i = 0; stakingInfos.size() > i; i++) {
                StakingInfo stakingInfo = stakingInfos.get(i);

                for (int j = 0; validatorList.size() > j; j++) {
                    Validator validator = validatorList.get(j);
                    if (validator.getOperatorAddress().equals(stakingInfo.getValidatorAddr())) {
                        validatorList.get(j).setDelegatedAmount(validatorList.get(j).getDelegatedAmount() + stakingInfo.getShares());
                    }
                }
            }
        }
    }

    private void updateValidatorListWithUnbondingInfos(ArrayList<Unbonding> unbondingInfos) {
        if (unbondingInfos != null) {
            for (Unbonding unbonding : unbondingInfos) {
                for (int j = 0; validatorList.size() > j; j++) {
                    Validator validator = validatorList.get(j);
                    if (validator.getOperatorAddress().equals(unbonding.getValidatorAddr())) {
                        BigDecimal delegatedAmount = new BigDecimal(validatorList.get(j).getDelegatedAmount());
                        String unstakingCompletionTime = null;
                        for (UnbondingEntries entries : unbonding.getEntries()) {
                            delegatedAmount = delegatedAmount.add(entries.getBalance());
                            unstakingCompletionTime = entries.getCompletionTime();
                        }
                        validatorList.get(j).setDelegatedAmount(delegatedAmount.doubleValue());
                        validatorList.get(j).setUnstakingCompletionTime(unstakingCompletionTime);
                    }
                }
            }
        }
    }

    private void updateMyValidators() {
        if (validatorList == null || validatorList.size() == 0) return;
        for (int i = validatorList.size() - 1; 0 <= i; i--) {
            if (validatorList.get(i).getDelegatedAmount() <= 0) {
                validatorList.remove(i);
            }
        }
    }

    private void setValidatorRank() {
        if (validatorList == null) return;

        for (int i = 0; validatorList.size() > i; i++) {
            validatorList.get(i).setRank((i + 1));
        }
    }

    private double getTotalPower(){
        try {
            double totalPower = 0;
            for (Validator validator : validatorList) {
                totalPower = totalPower + validator.getDelegatorShares();
            }
            return totalPower;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getActionbarTitle() {
        String title;
        switch (type) {
            case ValidatorListActivity.TYPE_VIEW_VALIDATORS:
                title = LocaleController.getString("validators", R.string.validators);
                break;
            case ValidatorListActivity.TYPE_VIEW_MY_VALIDATORS:
                title = LocaleController.getString("myValidators", R.string.myValidators);
                break;
            case ValidatorListActivity.TYPE_VIEW_UNSTAKING_VALIDATORS:
                title = LocaleController.getString("unstaking", R.string.unstaking) + " " + LocaleController.getString("validators", R.string.validators);
                break;
            default:
                title = LocaleController.getString("selectValidator", R.string.selectValidator);
        }
        return title;
    }

    private void finishWithResult(Validator validator) {
        if (type == ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_ALL && validator.getJailed()) {
            Toast.makeText(ValidatorListActivity.this, LocaleController.getString("jailedValidatorError", R.string.jailedValidatorError), Toast.LENGTH_LONG).show();
            return;
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("validatorName", validator.getDescription().getMoniker());
        resultIntent.putExtra("validatorAddress", validator.getOperatorAddress());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
