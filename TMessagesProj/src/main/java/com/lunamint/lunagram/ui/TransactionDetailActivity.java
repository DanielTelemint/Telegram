package com.lunamint.lunagram.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.TransactionClaimRewardDetailView;
import com.lunamint.lunagram.ui.view.TransactionSendDetailView;
import com.lunamint.lunagram.ui.view.TransactionStakeDetailView;
import com.lunamint.lunagram.ui.view.TransactionVoteDetailView;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.utils.BigDecimalUtil;
import com.lunamint.wallet.utils.NetworkUtil;
import com.lunamint.wallet.utils.NumberFormatter;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionDetailActivity extends LunagramBaseActivity {

    private String myAddress = "";
    private String txHash = "";

    private LinearLayout mainLayout;
    private LinearLayout loadingLayout;
    private TextView errorTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("transactionDetail", R.string.transactionDetail));

        myAddress = getIntent().getStringExtra("address");
        txHash = getIntent().getStringExtra("hash");

        FrameLayout frameLayout = new FrameLayout(this);

        ScrollView scrollView = new ScrollView(this);
        frameLayout.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(mainLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        mainLayout.setVisibility(View.GONE);

        loadingLayout = new LinearLayout(this);
        loadingLayout.setGravity(Gravity.CENTER);
        frameLayout.addView(loadingLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(this);
        loadingLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        errorTextview = new TextView(this);
        errorTextview.setTextColor(ActivityCompat.getColor(this, R.color.payneGrey));
        errorTextview.setTextSize(0, AndroidUtilities.dp(16));
        errorTextview.setGravity(Gravity.CENTER);
        errorTextview.setTypeface(Typeface.DEFAULT_BOLD);
        frameLayout.addView(errorTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 500, 20, 10, 20, 36));
        errorTextview.setVisibility(View.GONE);

        setContentView(frameLayout);

        getTransactionDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getTransactionDetail() {
        if (txHash == null || txHash.equals("")) {
            update(null);
            return;
        }

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getTransactionDetail(txHash).enqueue(new Callback<DefaultHistory>() {
            @Override
            public void onResponse(Call<DefaultHistory> call, Response<DefaultHistory> response) {
                if (response.code() == 200) {
                    update(response.body());
                } else {
                    update(null);
                }
            }

            @Override
            public void onFailure(Call<DefaultHistory> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(TransactionDetailActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(TransactionDetailActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void update(DefaultHistory result) {
        if (mainLayout == null) return;
        TextView titleTextview = new TextView(this);
        titleTextview.setTextColor(ActivityCompat.getColor(this, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);

        loadingLayout.setVisibility(View.GONE);
        if (result != null) {
            View transactionSendDetailView = null;
            String feeAmount = "0";

            try {
                if (result.getTx().getValue().getFee().getAmount() != null && result.getTx().getValue().getFee().getAmount().size() > 0)
                    feeAmount = result.getTx().getValue().getFee().getAmount().get(0).getAmount();

                switch (result.getTx().getValue().getMsg().get(0).getType()) {
                    case "cosmos-sdk/MsgSend":
                        String to = result.getTx().getValue().getMsg().get(0).getValue().get("to_address").getAsString();
                        if (myAddress != null && !myAddress.equals("") && myAddress.equals(to)) {
                            titleTextview.setText(LocaleController.getString("receive", R.string.receive));
                        } else {
                            titleTextview.setText(LocaleController.getString("send", R.string.send));
                        }

                        transactionSendDetailView = new TransactionSendDetailView(this,
                                TransactionSendDetailView.TYPE_RESULT,
                                true,
                                result.getResult(),
                                result.getHash(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("from_address").getAsString(),
                                to,
                                result.getTx().getValue().getMsg().get(0).getValue().get("amount").getAsJsonArray().get(0).getAsJsonObject().get("denom").getAsString(),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(result.getTx().getValue().getMsg().get(0).getValue().get("amount").getAsJsonArray().get(0).getAsJsonObject().get("amount").getAsString(), "4")),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(feeAmount, "4")),
                                result.getTx().getValue().getMemo(),
                                myAddress);
                        break;
                    case "cosmos-sdk/MsgDelegate":
                        titleTextview.setText(LocaleController.getString("delegate", R.string.delegate));
                        transactionSendDetailView = new TransactionStakeDetailView(this,
                                TransactionSendDetailView.TYPE_RESULT,
                                result.getResult(),
                                result.getHash(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("validator_address").getAsString(),
                                result.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("denom").getAsString(),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(result.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("amount").getAsString(), "4")),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(feeAmount, "4")));
                        break;
                    case "cosmos-sdk/MsgUndelegate":
                        titleTextview.setText(LocaleController.getString("unstake", R.string.unstake));
                        transactionSendDetailView = new TransactionStakeDetailView(this,
                                TransactionSendDetailView.TYPE_RESULT,
                                result.getResult(),
                                result.getHash(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("validator_address").getAsString(),
                                result.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("denom").getAsString(),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(result.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("amount").getAsString(), "4")),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(feeAmount, "4")));
                        break;
                    case "cosmos-sdk/MsgBeginRedelegate":
                        titleTextview.setText(LocaleController.getString("redelegate", R.string.redelegate));
                        transactionSendDetailView = new TransactionSendDetailView(this,
                                TransactionSendDetailView.TYPE_RESULT,
                                false,
                                result.getResult(),
                                result.getHash(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("delegator_address").getAsString(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("validator_dst_address").getAsString(),
                                result.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("denom").getAsString(),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(result.getTx().getValue().getMsg().get(0).getValue().getAsJsonObject("amount").get("amount").getAsString(), "4")),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(feeAmount, "4")),
                                result.getTx().getValue().getMemo(),
                                myAddress);
                        break;
                    case "cosmos-sdk/MsgWithdrawDelegationReward":
                        titleTextview.setText(LocaleController.getString("claimReward", R.string.claimReward));
                        transactionSendDetailView = new TransactionClaimRewardDetailView(this,
                                TransactionSendDetailView.TYPE_RESULT,
                                result.getResult(),
                                result.getHash(),
                                null,
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(feeAmount, "4")));
                        break;
                    case "cosmos-sdk/MsgVote":
                        titleTextview.setText(LocaleController.getString("vote", R.string.vote));
                        transactionSendDetailView = new TransactionVoteDetailView(this,
                                TransactionSendDetailView.TYPE_RESULT,
                                result.getResult(),
                                result.getHash(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("proposal_id").getAsString(),
                                result.getTx().getValue().getMsg().get(0).getValue().get("option").getAsString(),
                                NumberFormatter.getNumber(BigDecimalUtil.getNumberNano(feeAmount, "4")));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (transactionSendDetailView != null) {
                mainLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 20, 20, 36));

                mainLayout.addView(transactionSendDetailView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                mainLayout.setVisibility(View.VISIBLE);
                errorTextview.setVisibility(View.GONE);
            } else {
                errorTextview.setText(LocaleController.getString("failedGetTransactionError", R.string.failedGetTransactionError));
                errorTextview.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }
        } else {
            errorTextview.setText(LocaleController.getString("failedGetTransactionError", R.string.failedGetTransactionError));
            errorTextview.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }
    }
}
