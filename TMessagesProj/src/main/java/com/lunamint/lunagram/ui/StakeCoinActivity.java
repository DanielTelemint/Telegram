package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
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
import com.lunamint.lunagram.ui.view.AmountFormView;
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.lunagram.ui.view.FeeFormView;
import com.lunamint.lunagram.ui.view.SelectValidatorFormView;
import com.lunamint.lunagram.ui.view.TxConfirmView;
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
import com.lunamint.wallet.model.StakingInfo;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.utils.AnimUtil;
import com.lunamint.wallet.utils.CmdResultChecker;
import com.lunamint.wallet.utils.FileUtil;
import com.lunamint.wallet.utils.NetworkUtil;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.Parser;
import com.lunamint.wallet.utils.TimeUtil;
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

public class StakeCoinActivity extends LunagramBaseActivity {

    public final static int TYPE_STAKE = 0;
    public final static int TYPE_UNSTAKE = 1;
    public final static int TYPE_REDELEGATE = 2;

    private final int GET_TRANSACTION_RESULT_MAX_TRY_COUNT = 7;
    private final int GET_TRANSACTION_RESULT_DELAY = 3000;

    private int type;
    private int step = 0;
    private boolean isSending = false;
    private long currentTimeMillis = 0;

    private Coin availableCoin;

    private JsonObject tx;

    private String accountName = "";
    private String fromAddress = "";
    private String validatorName = "";
    private String validatorAddress = "";
    private String redelegateValidatorName = "";
    private String redelegateValidatorAddress = "";
    private AccountStatus accountStatus;

    private String hash = "";
    private int requestBlockHeight = -1;

    private ScrollView scrollView;

    private FrameLayout contentsLayout;
    private SelectValidatorFormView selectValidatorFormView;
    private AmountFormView amountFormView;
    private FeeFormView feeFormView;
    private DefaultFormView passwordFormView;
    private TxConfirmView txConfirmView;
    private TxProgressView txProgressView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getIntExtra("type", 0);
        accountName = getIntent().getStringExtra("accountName");
        fromAddress = getIntent().getStringExtra("address");
        validatorName = getIntent().getStringExtra("validatorName");
        validatorAddress = getIntent().getStringExtra("validatorAddress");

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(getActionbarTitle());

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.f));

        contentsLayout = new FrameLayout(this);
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nextButton = new DefaultButton(this, 4, true, LocaleController.getString("next", R.string.next), R.drawable.btn_grey, onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));

        setContentView(mainLayout);

        showSelectValidatorFormView(false);

        if (type == StakeCoinActivity.TYPE_STAKE) {
            getBalance();
        } else {
            getStake();
        }
    }

    @Override
    protected void onPause() {
        clearPasswordForm();
        super.onPause();
        if (step == 4) {
            step = 3;
            showPasswordFormView(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (currentTimeMillis > 0) FileUtil.deleteTx(StakeCoinActivity.this, currentTimeMillis);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveToBack();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        moveToBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ValidatorListActivity.ACTIVITY_RESULT_OK:
                    if (type != StakeCoinActivity.TYPE_REDELEGATE) {
                        validatorName = data.getStringExtra("validatorName");
                        validatorAddress = data.getStringExtra("validatorAddress");
                        selectValidatorFormView.updateValidator(validatorName);
                    } else {
                        redelegateValidatorName = data.getStringExtra("validatorName");
                        redelegateValidatorAddress = data.getStringExtra("validatorAddress");
                        if (redelegateValidatorAddress.length() > 0) {
                            selectValidatorFormView.updateRedelegateValidator(redelegateValidatorName);
                            nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
                        } else {
                            selectValidatorFormView.updateRedelegateValidator(null);
                            nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_grey);
                        }
                    }
                    break;
            }
        }
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
            case WalletManager.MAKE_TX_STAKE:
                onTransactionCreatedResult(cmdResult);
                break;
            case WalletManager.MAKE_TX_UNSTAKE:
                onTransactionCreatedResult(cmdResult);
                break;
            case WalletManager.MAKE_TX_REDELEGATE:
                onTransactionCreatedResult(cmdResult);
                break;
            case WalletManager.TX_SIGN:
                onTransactionSignedResult(cmdResult);
                break;
        }
    }

    private void signTransaction() {
        if (passwordFormView == null || accountStatus == null) return;
        isSending = true;

        String txPath = getFilesDir() + "/" + FileUtil.getTxFileName(currentTimeMillis);

        WalletManager.getInstance().sign(new ResultHandler(this), accountName, passwordFormView.getValue(), txPath, accountStatus);
    }

    private void makeTransactionStake() {
        if (tx != null && currentTimeMillis > 0)
            FileUtil.deleteTx(StakeCoinActivity.this, currentTimeMillis);
        if (amountFormView == null) return;

        WalletManager.getInstance().makeTransactionStake(new ResultHandler(this), fromAddress, passwordFormView.getValue(), validatorAddress, amountFormView.getValue(), feeFormView.getValue(), Blockchain.getInstance().getReserveDenom());
    }

    private void makeTransactionUnstake() {
        if (tx != null && currentTimeMillis > 0)
            FileUtil.deleteTx(StakeCoinActivity.this, currentTimeMillis);
        if (amountFormView == null) return;

        WalletManager.getInstance().makeTransactionUnstake(new ResultHandler(this), fromAddress, passwordFormView.getValue(), validatorAddress, amountFormView.getValue(), feeFormView.getValue(), Blockchain.getInstance().getReserveDenom());
    }

    private void makeTransactionRedelegate() {
        if (tx != null && currentTimeMillis > 0)
            FileUtil.deleteTx(StakeCoinActivity.this, currentTimeMillis);
        if (amountFormView == null) return;

        WalletManager.getInstance().makeTransactionRedelegate(new ResultHandler(this), fromAddress, passwordFormView.getValue(), validatorAddress, redelegateValidatorAddress, amountFormView.getValue(), feeFormView.getValue(), Blockchain.getInstance().getReserveDenom());
    }

    private void onTransactionCreatedResult(CmdResult cmdResult) {
        String errMsg = CmdResultChecker.checkCmdResult(cmdResult);
        if (errMsg == null) {
            setCurrentTimeMillis();
            if (FileUtil.writeTx(StakeCoinActivity.this, currentTimeMillis, cmdResult.getData())) {
                signTransaction();
            } else {
                step = 3;
                showPasswordFormView(true);
                if (passwordFormView != null)
                    passwordFormView.showError(LocaleController.getString("failedCreateTx", R.string.failedCreateTx));
                isSending = false;
            }
        } else {
            step = 3;
            showPasswordFormView(true);
            if (passwordFormView != null) passwordFormView.showError(errMsg);
            isSending = false;
        }
    }

    private void onTransactionSignedResult(CmdResult cmdResult) {
        String err = CmdResultChecker.checkCmdResult(cmdResult);

        if (err == null) {
            tx = Parser.getRawTransaction(cmdResult.getData());
            if (tx != null) {
                send();
                clearPasswordForm();
                return;
            } else {
                err = LocaleController.getString("unsupportTx", R.string.unsupportTx);
            }
        }
        step = 3;
        showPasswordFormView(true);
        if (passwordFormView != null) passwordFormView.showError(err);
        isSending = false;
    }

    private void send() {
        if (tx != null) {
            LcdService lcdService = ApiUtils.getLcdService();
            lcdService.sendTransaction(tx).enqueue(new Callback<SendResult>() {
                @Override
                public void onResponse(Call<SendResult> call, Response<SendResult> response) {
                    if (response.code() == 200 && response.body() != null) {
                        hash = response.body().getHash();
                        getLatestBlock();
                    } else {
                        String err;
                        try {
                            if (response.errorBody() != null && response.errorBody().string().contains("existing unbonding delegation found")) {
                                err = LocaleController.getString("aleadyUnbondingError", R.string.aleadyUnbondingError);
                            } else {
                                if (response.code() == 400 && response.errorBody() == null) {
                                    err = LocaleController.getString("unsupportTx", R.string.unsupportTx);
                                } else {
                                    err = LocaleController.getString("failedStake", R.string.failedStake) + "\nCode = " + response.code() + "\n" + response.errorBody().string();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            err = LocaleController.getString("unsupportTx", R.string.unsupportTx);
                        }
                        if (txProgressView != null) txProgressView.showError(err);
                        onSendFinished();
                    }
                }

                @Override
                public void onFailure(Call<SendResult> call, Throwable t) {
                    if (txProgressView == null) return;
                    if (NetworkUtil.isNetworkAvailable(StakeCoinActivity.this)) {
                        showNodeConnectionErrorAlert();
                    } else {
                        txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
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
                                txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake) + "\n\nRequest block height : " + requestBlockHeight + "\nLast checked block height : " + response.body().getBlock().getHeader().getHeight());
                                onSendFinished();
                            } else {
                                getTransactionResult();
                            }

                        } else {
                            txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
                            onSendFinished();
                        }
                    }

                    @Override
                    public void onFailure(Call<BlockInfo> call, Throwable t) {
                        if (txProgressView == null) return;
                        if (NetworkUtil.isNetworkAvailable(StakeCoinActivity.this)) {
                            txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
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
                            txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
                            onSendFinished();
                        }
                    } catch (Exception e) {
                        txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
                        onSendFinished();
                    }
                } else {
                    if (response.code() == 200 && response.body() != null) {
                        updateTxProgress(response.body().getResult(), response.body().getHash());
                        FileUtil.deleteTx(StakeCoinActivity.this, currentTimeMillis);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stakingChanged);
                    } else {
                        txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
                    }
                    onSendFinished();
                }
            }

            @Override
            public void onFailure(Call<DefaultHistory> call, Throwable t) {
                if (txProgressView == null) return;
                if (NetworkUtil.isNetworkAvailable(StakeCoinActivity.this)) {
                    txProgressView.showError(LocaleController.getString("failedStake", R.string.failedStake));
                } else {
                    txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                }
                onSendFinished();
            }
        });
    }

    private void clearPasswordForm() {
        if (passwordFormView == null) return;
        passwordFormView.clear();
    }

    private void getBalance() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getBalances(fromAddress).enqueue(new Callback<ArrayList<Coin>>() {
            @Override
            public void onResponse(Call<ArrayList<Coin>> call, Response<ArrayList<Coin>> response) {
                updateBalance(response);
                if (response.code() == 200)
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.balanceChanged, response);
            }

            @Override
            public void onFailure(Call<ArrayList<Coin>> call, Throwable t) {
                if (t.getMessage().contains("End of input at")) {
                    updateBalanceEmpty();
                } else {
                    if (NetworkUtil.isNetworkAvailable(StakeCoinActivity.this)) {
                        showNodeConnectionErrorAlert();
                    } else {
                        Toast.makeText(StakeCoinActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void getStake() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getStakingInfo(fromAddress, validatorAddress).enqueue(new Callback<StakingInfo>() {
            @Override
            public void onResponse(Call<StakingInfo> call, Response<StakingInfo> response) {
                updateStake(response);
            }

            @Override
            public void onFailure(Call<StakingInfo> call, Throwable t) {
                if (t.getMessage().contains("End of input at")) {
                    updateBalanceEmpty();
                } else {
                    if (NetworkUtil.isNetworkAvailable(StakeCoinActivity.this)) {
                        showNodeConnectionErrorAlert();
                    } else {
                        Toast.makeText(StakeCoinActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void getAccountStatus() {
        isSending = true;
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getAccountStatus(fromAddress).enqueue(new Callback<AccountStatus>() {
            @Override
            public void onResponse(Call<AccountStatus> call, Response<AccountStatus> response) {
                if (response.code() == 200) {
                    accountStatus = response.body();
                    if (type == StakeCoinActivity.TYPE_STAKE) {
                        makeTransactionStake();
                    } else if (type == StakeCoinActivity.TYPE_UNSTAKE) {
                        makeTransactionUnstake();
                    } else {
                        makeTransactionRedelegate();
                    }
                } else {
                    step = 3;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                    isSending = false;
                }
            }

            @Override
            public void onFailure(Call<AccountStatus> call, Throwable t) {
                if (NetworkUtil.isNetworkAvailable(StakeCoinActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    step = 3;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                }
                isSending = false;
            }
        });
    }

    private void updateBalance(Response<ArrayList<Coin>> response) {
        if (response != null) {
            switch (response.code()) {
                case 200:
                    ArrayList<Coin> coins = response.body();
                    if (coins != null && coins.size() > 0) {
                        boolean hashReserveCoin = false;
                        for (int i = 0; coins.size() > i; i++) {
                            if (coins.get(i).getDenom().equals(Blockchain.getInstance().getReserveDenom())) {
                                availableCoin = coins.get(i);
                                updateAvailableCoin();
                                hashReserveCoin = true;
                            }
                        }
                        if (!hashReserveCoin) {
                            availableCoin = getZeroAmountCoin();
                            updateAvailableCoin();
                        }
                    } else {
                        availableCoin = getZeroAmountCoin();
                        updateAvailableCoin();
                    }
                    break;
                default:
                    availableCoin = getZeroAmountCoin();
                    updateAvailableCoin();
                    break;
            }
        } else {
            availableCoin = getZeroAmountCoin();
            updateAvailableCoin();
        }
    }

    private void updateBalanceEmpty() {
        availableCoin = getZeroAmountCoin();
        updateAvailableCoin();
    }

    private void updateStake(Response<StakingInfo> response) {
        if (response != null) {
            switch (response.code()) {
                case 200:
                    StakingInfo responseCoin = response.body();
                    if (responseCoin != null) {
                        Coin coin = new Coin();
                        coin.setDenom(Blockchain.getInstance().getReserveDenom());
                        coin.setAmount(responseCoin.getShares() + "");
                        availableCoin = coin;
                        updateAvailableCoin();
                    } else {
                        availableCoin = getZeroAmountCoin();
                        updateAvailableCoin();
                    }
                    break;
                default:
                    availableCoin = getZeroAmountCoin();
                    updateAvailableCoin();
                    break;
            }
        } else {
            availableCoin = getZeroAmountCoin();
            updateAvailableCoin();
        }
    }

    private void updateAvailableCoin() {
        if (amountFormView == null) return;
        amountFormView.update(availableCoin);
    }

    private void updateTxProgress(boolean result, String txHash) {
        if (txProgressView == null) return;
        switch (type) {
            case StakeCoinActivity.TYPE_STAKE:
                txProgressView.update(TxProgressView.TYPE_STAKE, result, txHash, "", validatorName, Blockchain.getInstance().getReserveDenom(), NumberFormatter.getNumber(amountFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", fromAddress);
                break;
            case StakeCoinActivity.TYPE_UNSTAKE:
                txProgressView.update(TxProgressView.TYPE_UNSTAKE, result, txHash, "", validatorName, Blockchain.getInstance().getReserveDenom(), NumberFormatter.getNumber(amountFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", fromAddress);
                break;
            case StakeCoinActivity.TYPE_REDELEGATE:
                txProgressView.update(TxProgressView.TYPE_REDELEGATE, result, txHash, validatorName, redelegateValidatorName, Blockchain.getInstance().getReserveDenom(), NumberFormatter.getNumber(amountFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", fromAddress);
                break;
        }
    }

    private void varifyValidator() {
        if (VarifyUtil.isValidValidator(this, type, validatorAddress, redelegateValidatorAddress)) {
            step = 1;
            showAmountFormView(false);
        }
    }

    private void varifyAmount(String amount) {
        if (amountFormView == null || availableCoin == null) return;
        if (amount.length() != 0) {
            try {
                BigDecimal bamount = new BigDecimal(amount);
                if (bamount.compareTo(BigDecimal.ZERO) < 1) {
                    amountFormView.showError(LocaleController.getString("amountInvalidError", R.string.amountInvalidError));
                } else {
                    if (bamount.compareTo(new BigDecimal(availableCoin.getAmount())) < 1) {
                        //scrollView.smoothScrollTo(0, 0);
                        hideKeyboard();
                        step = 2;
                        showFeeFormView(false);
                    } else {
                        amountFormView.showError(LocaleController.getString("notEnoughBalance", R.string.notEnoughBalance));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                amountFormView.showError(LocaleController.getString("amountInvalidError", R.string.amountInvalidError));
            }
        } else {
            amountFormView.showError(LocaleController.getString("amountEmptyError", R.string.amountEmptyError));
        }
    }

    private void varifyFee(String fee) {
        if (amountFormView == null || feeFormView == null) return;
        if (type == StakeCoinActivity.TYPE_STAKE) {
            if (fee.length() != 0) {
                try {
                    BigDecimal bamount = new BigDecimal(fee);
                    if (bamount.compareTo(new BigDecimal("0.0005")) < 0) {
                        feeFormView.showError(LocaleController.getString("feeNotEnoughError", R.string.feeNotEnoughError));
                    } else if (bamount.compareTo(BigDecimal.ONE) == 1) {
                        feeFormView.showError(LocaleController.getString("feeInvalidError", R.string.feeInvalidError));
                    } else {
                        bamount = bamount.add(new BigDecimal(amountFormView.getValueOrigin()));
                        if (bamount.compareTo(new BigDecimal(availableCoin.getAmount())) < 1) {
                            scrollView.smoothScrollTo(0, 0);
                            hideKeyboard();
                            step = 3;
                            showPasswordFormView(false);
                            if (hasPassword() && isEnabledFingerprint(accountName))
                                checkFingerprint();
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
        } else {
            BigDecimal bamount = new BigDecimal(fee);
            if (bamount.compareTo(new BigDecimal("0.0005")) < 0) {
                feeFormView.showError(LocaleController.getString("feeNotEnoughError", R.string.feeNotEnoughError));
            } else if (bamount.compareTo(BigDecimal.ONE) == 1) {
                feeFormView.showError(LocaleController.getString("feeInvalidError", R.string.feeInvalidError));
            } else {
                hideKeyboard();
                step = 3;
                showPasswordFormView(false);
                if (hasPassword() && isEnabledFingerprint(accountName)) checkFingerprint();
            }
        }
    }

    private void varifyPassword(String pwd) {
        if (passwordFormView == null) return;
        if (pwd == null || pwd.length() < 8) {
            passwordFormView.showError(LocaleController.getString("pwdInvalidError", R.string.pwdInvalidError));
        } else {
            step = 4;
            showTxConfirmView();
            if (txConfirmView != null) {
                switch (type) {
                    case StakeCoinActivity.TYPE_STAKE:
                        txConfirmView.update(TxProgressView.TYPE_STAKE, null, "", validatorName, Blockchain.getInstance().getReserveDenom(), NumberFormatter.getNumber(amountFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", fromAddress);
                        break;
                    case StakeCoinActivity.TYPE_UNSTAKE:
                        txConfirmView.update(TxProgressView.TYPE_UNSTAKE, null, "", validatorName, Blockchain.getInstance().getReserveDenom(), NumberFormatter.getNumber(amountFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", fromAddress);
                        break;
                    case StakeCoinActivity.TYPE_REDELEGATE:
                        txConfirmView.update(TxProgressView.TYPE_REDELEGATE, null, validatorName, redelegateValidatorName, Blockchain.getInstance().getReserveDenom(), NumberFormatter.getNumber(amountFormView.getValueOrigin()), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", fromAddress);
                        break;
                }
            }
        }
    }

    private void showSelectValidatorFormView(boolean isMoveToBack) {
        if (selectValidatorFormView == null) {
            selectValidatorFormView = new SelectValidatorFormView(this, type, validatorName, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == StakeCoinActivity.TYPE_REDELEGATE)
                        showValidatorListActivity(ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_ALL);

                    /*if (type == StakeCoinActivity.TYPE_STAKE || type == StakeCoinActivity.TYPE_REDELEGATE) {
                        showValidatorListActivity(ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_ALL);
                    } else {
                        showValidatorListActivity(ValidatorListActivity.TYPE_SELECT_VALIDATOR_FROM_MY);
                    }*/
                }
            });
            contentsLayout.addView(selectValidatorFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(selectValidatorFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, selectValidatorFormView, false);
        }

        if (type == StakeCoinActivity.TYPE_REDELEGATE) {
            if (redelegateValidatorAddress.equals("")) {
                nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_grey);
            } else {
                nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
            }
        } else {
            if (validatorAddress.equals("")) {
                nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_grey);
            } else {
                nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
            }

        }
        currentView = selectValidatorFormView;
    }

    private void showAmountFormView(boolean isMoveToBack) {

        if (amountFormView == null) {
            String title = "";
            String desc = "";
            switch (type) {
                case StakeCoinActivity.TYPE_STAKE:
                    title = LocaleController.getString("amountToStake", R.string.amountToStake);
                    desc = LocaleController.getString("enterAmountStake", R.string.enterAmountStake);
                    break;
                case StakeCoinActivity.TYPE_UNSTAKE:
                    title = LocaleController.getString("amountToUntake", R.string.amountToUntake);
                    desc = LocaleController.getString("enterAmountUnstake", R.string.enterAmountUnstake);
                    break;
                case StakeCoinActivity.TYPE_REDELEGATE:
                    title = LocaleController.getString("amountToRestake", R.string.amountToRestake);
                    desc = LocaleController.getString("enterAmountRestake", R.string.enterAmountRestake);
                    break;
            }
            amountFormView = new AmountFormView(this, true, title, desc, availableCoin, "");
            contentsLayout.addView(amountFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(amountFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, amountFormView, false);
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = amountFormView;
    }

    private void showFeeFormView(boolean isMoveToBack) {
        hideKeyboard();
        if (feeFormView == null) {
            feeFormView = new FeeFormView(this);
            contentsLayout.addView(feeFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(feeFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, feeFormView, false);
        }
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

    private void showTxConfirmView() {
        hideKeyboard();
        if (txConfirmView != null) contentsLayout.removeView(txConfirmView);

        String title = "";
        switch (type) {
            case StakeCoinActivity.TYPE_STAKE:
                title = LocaleController.getString("confirmStaking", R.string.confirmStaking);
                break;
            case StakeCoinActivity.TYPE_UNSTAKE:
                title = LocaleController.getString("confirmStaking", R.string.confirmUnstake);
                break;
            case StakeCoinActivity.TYPE_REDELEGATE:
                title = LocaleController.getString("confirmStaking", R.string.confirmRestake);
                break;
        }
        txConfirmView = new TxConfirmView(this, title);
        contentsLayout.addView(txConfirmView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        AnimUtil.changeView(currentView, txConfirmView, false);
        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = txConfirmView;
    }

    private void showTxProgressView() {
        hideKeyboard();
        txProgressView = new TxProgressView(this);
        contentsLayout.addView(txProgressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        AnimUtil.changeView(currentView, txProgressView, false);
        nextButton.update(LocaleController.getString("done", R.string.done), R.drawable.btn_grey);
        currentView = txProgressView;
    }

    private Coin getZeroAmountCoin() {
        Coin coin = new Coin();
        coin.setDenom(Blockchain.getInstance().getReserveDenom());
        coin.setAmount("0");
        return coin;
    }

    private String getActionbarTitle() {
        String title = "";
        switch (type) {
            case StakeCoinActivity.TYPE_STAKE:
                title = LocaleController.getString("stake", R.string.stake);
                break;
            case StakeCoinActivity.TYPE_UNSTAKE:
                title = LocaleController.getString("unstake", R.string.unstake);
                break;
            case StakeCoinActivity.TYPE_REDELEGATE:
                title = LocaleController.getString("redelegate", R.string.redelegate);
                break;
        }
        return title;
    }

    private void showValidatorListActivity(int type) {
        Intent intent = new Intent(StakeCoinActivity.this, ValidatorListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", fromAddress);
        startActivityForResult(intent, 200);
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

    private void moveToBack() {
        switch (step) {
            case 0:
                finish();
                break;
            case 1:
                step = 0;
                showSelectValidatorFormView(true);
                break;
            case 2:
                step = 1;
                showAmountFormView(true);
                break;
            case 3:
                step = 2;
                showFeeFormView(true);
                break;
            case 4:
                step = 3;
                showPasswordFormView(true);
                break;
        }
    }

    private View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (step) {
                case 0:
                    varifyValidator();
                    break;
                case 1:
                    if (amountFormView != null) varifyAmount(amountFormView.getValueOrigin());
                    break;
                case 2:
                    if (feeFormView != null) varifyFee(feeFormView.getValueOrigin());
                    break;
                case 3:
                    if (passwordFormView != null) varifyPassword(passwordFormView.getValue());
                    break;
                case 4:
                    step = 5;
                    showTxProgressView();
                    getAccountStatus();
                    break;
                case 5:
                    if (!isSending) finish();
                    break;
            }
        }
    };
}
