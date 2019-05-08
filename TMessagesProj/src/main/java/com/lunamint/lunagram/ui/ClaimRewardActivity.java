package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.SecureKeyStore;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.lunagram.ui.view.FeeFormView;
import com.lunamint.lunagram.ui.view.TxProgressView;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.AccountStatus;
import com.lunamint.wallet.model.BlockInfo;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.model.SendResult;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.utils.AnimUtil;
import com.lunamint.wallet.utils.CmdResultChecker;
import com.lunamint.wallet.utils.FileUtil;
import com.lunamint.wallet.utils.NetworkUtil;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.Parser;
import com.lunamint.wallet.utils.TimeUtil;
import com.lunamint.wallet.utils.TokenUtil;
import com.lunamint.wallet.utils.VarifyUtil;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.Components.LayoutHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimRewardActivity extends LunagramBaseActivity {

    private final int GET_TRANSACTION_RESULT_MAX_TRY_COUNT = 7;
    private final int GET_TRANSACTION_RESULT_DELAY = 3000;

    private int step = 0;
    private boolean isSending = false;
    private long currentTimeMillis = 0;
    private boolean needToGoNext = false;

    private ArrayList<Coin> coins;

    private String accountName = "";
    private String address = "";
    private String rewards = "";
    private AccountStatus accountStatus;

    private String hash = "";
    private int requestBlockHeight = -1;

    private ScrollView scrollView;
    private FrameLayout contentsLayout;
    private FeeFormView feeFormView;
    private DefaultFormView passwordFormView;
    private TxProgressView txProgressView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("claimReward", R.string.claimReward));

        accountName = getIntent().getStringExtra("accountName");
        address = getIntent().getStringExtra("address");
        rewards = getIntent().getStringExtra("rewards");

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        contentsLayout = new FrameLayout(this);
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nextButton = new DefaultButton(this, 4, true, LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue, onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));

        setContentView(mainLayout);

        showFeeFormView();

        getBalance();
    }

    @Override
    protected void onPause() {
        clearPasswordForm();
        super.onPause();
    }

    @Override
    protected void onCheckFingerprintDone() {
        super.onCheckFingerprintDone();
        if (passwordFormView == null) return;

        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String pwd = SecureKeyStore.getInstance().decrypt(getApplicationContext(), pref.getString("W-" + accountName.replaceAll(" ", ""), ""));
        passwordFormView.setValue(pwd);
        passwordFormView.clearFocus();

        if (pwd.length() > 0) varifyPassword(pwd);
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        CmdResult cmdResult = ((CmdResult) msg.getData().getSerializable("result"));
        switch (msg.what) {
            case WalletManager.MAKE_TX_CLAIM_REWARD:
                onTransactionCreatedResult(cmdResult);
                break;
            case WalletManager.TX_SIGN:
                onTransactionSignedResult(cmdResult);
                break;
        }
    }

    private void getBalance() {
        if (!VarifyUtil.isValidCosmosAddress(address)) return;
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getBalances(address).enqueue(new Callback<ArrayList<Coin>>() {
            @Override
            public void onResponse(Call<ArrayList<Coin>> call, Response<ArrayList<Coin>> response) {
                if (response.code() == 200) {
                    coins = response.body();
                    if (needToGoNext) {
                        needToGoNext = false;
                        hideProgress();
                        if (feeFormView != null) varifyFee(feeFormView.getValueOrigin());
                    }
                    if (response.body() != null)
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.balanceChanged, response);
                } else {
                    showNodeConnectionErrorAlert();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Coin>> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(ClaimRewardActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(ClaimRewardActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
    }

    private void getAccountStatus() {
        isSending = true;
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getAccountStatus(address).enqueue(new Callback<AccountStatus>() {
            @Override
            public void onResponse(Call<AccountStatus> call, Response<AccountStatus> response) {
                if (response.code() == 200) {
                    accountStatus = response.body();
                    makeTransactionClaimReward();
                } else {
                    step = 4;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                    isSending = false;
                }
            }

            @Override
            public void onFailure(Call<AccountStatus> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(ClaimRewardActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    step = 4;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                }
                isSending = false;
            }
        });
    }

    private void signTransaction() {
        if (txProgressView == null || accountStatus == null) return;

        String txPath = getFilesDir() + "/" + FileUtil.getTxFileName(currentTimeMillis);

        WalletManager.getInstance().sign(new ResultHandler(this), accountName, passwordFormView.getValue(), txPath, accountStatus);
    }

    private void send(JsonObject signedTx) {
        LcdService lcdService = ApiUtils.getLcdService();

        if (signedTx != null) {
            lcdService.sendTransaction(signedTx).enqueue(new Callback<SendResult>() {
                @Override
                public void onResponse(Call<SendResult> call, Response<SendResult> response) {
                    if (response.code() == 200 && response.body() != null) {
                        hash = response.body().getHash();
                        getLatestBlock();
                    } else {
                        String err;
                        try {
                            String errorBody = "";
                            if (response.errorBody() != null)
                                errorBody = response.errorBody().string();
                            err = LocaleController.getString("failedClaimReward", R.string.failedClaimReward) + "\nCode = " + String.valueOf(response.code()) + "\n" + errorBody;
                        } catch (Exception e) {
                            err = LocaleController.getString("unsupportTx", R.string.unsupportTx);
                        }
                        if (txProgressView != null) txProgressView.showError(err);
                        onSendFinished();
                    }

                }

                @Override
                public void onFailure(Call<SendResult> call, Throwable t) {
                    if (txProgressView == null) return;
                    if (NetworkUtil.isNetworkAvailable(ClaimRewardActivity.this)) {
                        showNodeConnectionErrorAlert();
                    } else {
                        Toast.makeText(ClaimRewardActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                    }
                    onSendFinished();
                }
            });
        } else {
            if (txProgressView != null)
                txProgressView.showError(LocaleController.getString("nullPointerTx", R.string.nullPointerTx));
            onSendFinished();
        }
    }

    private void getLatestBlock() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LcdService lcdService = ApiUtils.getLcdService();
                lcdService.getLatestBlock().enqueue(new Callback<BlockInfo>() {
                    @Override
                    public void onResponse(Call<BlockInfo> call, Response<BlockInfo> response) {
                        if (response.code() == 200 && response.body() != null && response.body().getBlock() != null && response.body().getBlock().getHeader() != null) {
                            if (requestBlockHeight < 0)
                                requestBlockHeight = response.body().getBlock().getHeader().getHeight();

                            if (requestBlockHeight + GET_TRANSACTION_RESULT_MAX_TRY_COUNT <= response.body().getBlock().getHeader().getHeight()) {
                                txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward) + "\n\nRequest block height : " + requestBlockHeight + "\nLast checked block height : " + response.body().getBlock().getHeader().getHeight());
                                onSendFinished();
                            } else {
                                getTransactionResult();
                            }

                        } else {
                            txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward));
                            onSendFinished();
                        }
                    }

                    @Override
                    public void onFailure(Call<BlockInfo> call, Throwable t) {
                        if (txProgressView == null) return;
                        if (NetworkUtil.isNetworkAvailable(ClaimRewardActivity.this)) {
                            txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward));
                        } else {
                            txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                        }
                        onSendFinished();
                    }
                });
            }
        }, GET_TRANSACTION_RESULT_DELAY);
    }

    private void getTransactionResult() {
        if (hash == null || hash.equals("")) {
            txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
            return;
        }

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getTransactionDetail(hash).enqueue(new Callback<DefaultHistory>() {
            @Override
            public void onResponse(Call<DefaultHistory> call, Response<DefaultHistory> response) {
                if (response.errorBody() != null) {
                    try {
                        if (response.errorBody().string().contains("not found")) {
                            getLatestBlock();
                        } else {
                            txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward));
                            onSendFinished();
                        }
                    } catch (Exception e) {
                        txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward));
                        onSendFinished();
                    }
                } else {
                    if (response.code() == 200 && response.body() != null) {
                        updateTxProgress(response.body().getResult(), response.body().getHash());
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.transactionCreated, response.body().getHash());
                        FileUtil.deleteTx(ClaimRewardActivity.this, currentTimeMillis);
                    } else {
                        txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward));
                    }
                    onSendFinished();
                }
            }

            @Override
            public void onFailure(Call<DefaultHistory> call, Throwable t) {
                if (txProgressView == null) return;
                if (NetworkUtil.isNetworkAvailable(ClaimRewardActivity.this)) {
                    txProgressView.showError(LocaleController.getString("failedClaimReward", R.string.failedClaimReward));
                } else {
                    txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                }
                onSendFinished();
            }
        });
    }

    private void makeTransactionClaimReward() {
        if (passwordFormView == null) return;

        WalletManager.getInstance().makeTxClaimReward(new ResultHandler(this), address, passwordFormView.getValue(), feeFormView.getValue());
    }

    private void onTransactionCreatedResult(CmdResult cmdResult) {
        String errMsg = CmdResultChecker.checkCmdResult(cmdResult);

        if (errMsg == null) {
            setCurrentTimeMillis();
            if (FileUtil.writeTx(ClaimRewardActivity.this, currentTimeMillis, cmdResult.getData())) {
                signTransaction();
            } else {
                if (txProgressView != null)
                    txProgressView.showError(LocaleController.getString("failedCreateTx", R.string.failedCreateTx));
                isSending = false;
            }
        } else {
            step = 1;
            showPasswordFormView(true);
            if (passwordFormView != null) passwordFormView.showError(errMsg);
            isSending = false;
        }
    }

    private void onTransactionSignedResult(CmdResult cmdResult) {
        String err = CmdResultChecker.checkCmdResult(cmdResult);

        if (err == null) {
            JsonObject tx = Parser.getRawTransaction(cmdResult.getData());
            if (tx != null) {
                clearPasswordForm();
                send(tx);
                return;
            } else {
                err = LocaleController.getString("unsupportTx", R.string.unsupportTx);
            }
        }
        step = 1;
        showPasswordFormView(true);
        if (passwordFormView != null) passwordFormView.showError(err);
        isSending = false;
    }

    private void clearPasswordForm() {
        if (passwordFormView == null) return;
        passwordFormView.clear();
    }

    private void varifyFee(String fee) {
        if (feeFormView == null) return;
        if (coins == null) {
            needToGoNext = true;
            showProgress();
            return;
        }
        if (fee.length() != 0) {
            try {
                BigDecimal bamount = new BigDecimal(fee);
                if (bamount.compareTo(new BigDecimal("0.0005")) < 0) {
                    feeFormView.showError(LocaleController.getString("feeNotEnoughError", R.string.feeNotEnoughError));
                } else if (bamount.compareTo(BigDecimal.ONE) == 1) {
                    feeFormView.showError(LocaleController.getString("feeInvalidError", R.string.feeInvalidError));
                } else {
                    if (bamount.compareTo(new BigDecimal(TokenUtil.getCoin(coins, Blockchain.getInstance().getReserveDenom()).getAmount())) < 1) {
                        scrollView.smoothScrollTo(0, 0);
                        hideKeyboard();
                        step = 1;
                        showPasswordFormView(false);
                        if (hasPassword() && isEnabledFingerprint(accountName)) checkFingerprint();
                    } else {
                        feeFormView.showError(LocaleController.getString("notEnoughBalance", R.string.notEnoughBalance));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                feeFormView.showError(LocaleController.getString("amountInvalidError", R.string.amountInvalidError));
            }
        } else {
            feeFormView.showError(LocaleController.getString("amountEmptyError", R.string.amountEmptyError));
        }
    }

    private void varifyPassword(String pwd) {
        if (passwordFormView == null) return;
        if (pwd == null || pwd.length() < 8) {
            passwordFormView.showError(LocaleController.getString("pwdInvalidError", R.string.pwdInvalidError));
        } else {
            step = 2;
            showTxProgressView();
            getAccountStatus();
        }
    }

    private void showFeeFormView() {
        hideKeyboard();
        feeFormView = new FeeFormView(this);
        contentsLayout.addView(feeFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        AnimUtil.changeView(currentView, feeFormView, false);
        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = feeFormView;
    }

    private void showPasswordFormView(boolean isMoveToBack) {
        if (passwordFormView == null) {
            passwordFormView = new DefaultFormView(this, true, true, false, LocaleController.getString("password", R.string.password), LocaleController.getString("differentWalletsDifferentPasswords", R.string.differentWalletsDifferentPasswords), null, null);
            contentsLayout.addView(passwordFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(passwordFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, passwordFormView, false);
        }
        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        if (!isEnabledFingerprint(accountName)) passwordFormView.setFocus();
        currentView = passwordFormView;
    }

    private void showTxProgressView() {
        hideKeyboard();
        txProgressView = new TxProgressView(this);
        contentsLayout.addView(txProgressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        AnimUtil.changeView(currentView, txProgressView, false);
        nextButton.update(LocaleController.getString("done", R.string.done), R.drawable.btn_grey);
        currentView = txProgressView;
    }

    private void updateTxProgress(boolean result, String txHash) {
        if (txProgressView == null) return;
        txProgressView.update(TxProgressView.TYPE_CLAIM_REWARDS, result, txHash, null, null, null, NumberFormatter.getNumber(rewards), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", address);
    }

    private void onSendFinished() {
        isSending = false;
        nextButton.update(LocaleController.getString("done", R.string.done), R.drawable.btn_radius4_blue);
    }

    private boolean hasPassword() {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String pwd = pref.getString("W-" + accountName.replaceAll(" ", ""), "");
        return !pwd.equals("");
    }

    private void setCurrentTimeMillis() {
        currentTimeMillis = TimeUtil.getCurrentTimeMillis();
    }

    private View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (step) {
                case 0:
                    if (feeFormView != null) varifyFee(feeFormView.getValueOrigin());
                    break;
                case 1:
                    if (passwordFormView != null) varifyPassword(passwordFormView.getValue());
                    break;
                case 2:
                    if (!isSending) finish();
                    break;
            }
        }
    };
}
