package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.lunagram.ui.view.FeeFormView;
import com.lunamint.lunagram.ui.view.TxConfirmView;
import com.lunamint.lunagram.ui.view.TxProgressView;
import com.lunamint.lunagram.ui.view.VoteFormView;
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

public class VoteActivity extends LunagramBaseActivity {

    private final int GET_TRANSACTION_RESULT_MAX_TRY_COUNT = 7;
    private final int GET_TRANSACTION_RESULT_DELAY = 3000;

    private int step = 0;
    private boolean isSending = false;
    private long currentTimeMillis = 0;
    private boolean needToGoNext = false;

    private ArrayList<Coin> coins;

    private JsonObject tx;

    private String accountName = "";
    private String address = "";
    private String proposalId = "";
    private AccountStatus accountStatus;

    private String hash = "";
    private int requestBlockHeight = -1;

    private ScrollView scrollView;
    private FrameLayout contentsLayout;
    private VoteFormView voteFormView;
    private FeeFormView feeFormView;
    private DefaultFormView passwordFormView;
    private TxConfirmView txConfirmView;
    private TxProgressView txProgressView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("vote", R.string.vote));

        accountName = getIntent().getStringExtra("accountName");
        address = getIntent().getStringExtra("address");
        proposalId = getIntent().getStringExtra("proposalId");

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        contentsLayout = new FrameLayout(this);
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nextButton = new DefaultButton(this, 4, true, LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue, onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));

        setContentView(mainLayout);

        showVoteFormView(false);

        getBalance();
    }

    @Override
    protected void onPause() {
        clearPasswordForm();
        super.onPause();
        if (step == 3) {
            step = 2;
            showPasswordFormView(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (currentTimeMillis > 0) FileUtil.deleteTx(VoteActivity.this, currentTimeMillis);
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
            case WalletManager.MAKE_TX_VOTE:
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
                if (NetworkUtil.isNetworkAvailable(VoteActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    Toast.makeText(VoteActivity.this, LocaleController.getString("networkError", R.string.networkError), Toast.LENGTH_LONG).show();
                    finish();
                }
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
                    makeTransactionVote();
                } else {
                    step = 2;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                    isSending = false;
                }
            }

            @Override
            public void onFailure(Call<AccountStatus> call, Throwable t) {
                isSending = false;
                if (NetworkUtil.isNetworkAvailable(VoteActivity.this)) {
                    showNodeConnectionErrorAlert();
                } else {
                    step = 2;
                    showPasswordFormView(true);
                    if (passwordFormView != null)
                        passwordFormView.showError(LocaleController.getString("failedLoadAccountError", R.string.failedLoadAccountError));
                }
                isSending = false;
            }
        });
    }

    private void signTransaction() {
        if (passwordFormView == null || accountStatus == null) return;

        String txPath = getFilesDir() + "/" + FileUtil.getTxFileName(currentTimeMillis);

        WalletManager.getInstance().sign(new ResultHandler(this), accountName, passwordFormView.getValue(), txPath, accountStatus);
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
                            String errorBody = "";
                            if (response.errorBody() != null)
                                errorBody = response.errorBody().string();
                            err = LocaleController.getString("failedVote", R.string.failedVote) + "\nCode = " + String.valueOf(response.code()) + "\n" + errorBody;
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
                    if (NetworkUtil.isNetworkAvailable(VoteActivity.this)) {
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
                                txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote) + "\n\nRequest block height : " + requestBlockHeight + "\nLast checked block height : " + response.body().getBlock().getHeader().getHeight());
                                onSendFinished();
                            } else {
                                getTransactionResult();
                            }

                        } else {
                            txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote));
                            onSendFinished();
                        }
                    }

                    @Override
                    public void onFailure(Call<BlockInfo> call, Throwable t) {
                        if (txProgressView == null) return;
                        if (NetworkUtil.isNetworkAvailable(VoteActivity.this)) {
                            txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote));
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
                            txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote));
                            onSendFinished();
                        }
                    } catch (Exception e) {
                        txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote));
                        onSendFinished();
                    }
                } else {
                    if (response.code() == 200 && response.body() != null) {
                        updateTxProgress(response.body().getResult(), response.body().getHash());
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.transactionCreated, response.body().getHash());
                        FileUtil.deleteTx(VoteActivity.this, currentTimeMillis);
                    } else {
                        txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote));
                    }
                    onSendFinished();
                }
            }

            @Override
            public void onFailure(Call<DefaultHistory> call, Throwable t) {
                if (txProgressView == null) return;
                if (NetworkUtil.isNetworkAvailable(VoteActivity.this)) {
                    txProgressView.showError(LocaleController.getString("failedVote", R.string.failedVote));
                } else {
                    txProgressView.showError(LocaleController.getString("networkError", R.string.networkError));
                }
                onSendFinished();
            }
        });
    }

    private void makeTransactionVote() {
        if (tx != null && currentTimeMillis > 0)
            FileUtil.deleteTx(VoteActivity.this, currentTimeMillis);
        if (passwordFormView == null) return;

        WalletManager.getInstance().makeTxVote(new ResultHandler(this), address, passwordFormView.getValue(), proposalId, voteFormView.getValue(), feeFormView.getValue());
    }

    private void onTransactionCreatedResult(CmdResult cmdResult) {
        String errMsg = CmdResultChecker.checkCmdResult(cmdResult);
        if (errMsg == null) {
            setCurrentTimeMillis();
            if (FileUtil.writeTx(VoteActivity.this, currentTimeMillis, cmdResult.getData())) {
                signTransaction();
            } else {
                step = 2;
                showPasswordFormView(true);
                if (passwordFormView != null)
                    passwordFormView.showError(LocaleController.getString("failedCreateTx", R.string.failedCreateTx));
                isSending = false;
            }
        } else {
            step = 2;
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
        step = 2;
        showPasswordFormView(false);
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
                        step = 2;
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
            step = 3;
            showTxConfirmView();
            if (txConfirmView != null)
                txConfirmView.update(TxProgressView.TYPE_VOTE, null, null, proposalId, null, voteFormView.getValue(), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", address);
        }
    }

    private void showVoteFormView(boolean isMoveToBack) {
        hideKeyboard();
        if (feeFormView == null) {
            voteFormView = new VoteFormView(this);
            contentsLayout.addView(voteFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
        if (isMoveToBack) {
            AnimUtil.changePrevView(voteFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, voteFormView, false);
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        currentView = voteFormView;
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

        txConfirmView = new TxConfirmView(this, LocaleController.getString("confirmVote", R.string.confirmVote));
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

    private void updateTxProgress(boolean result, String txHash) {
        if (txProgressView == null) return;
        txProgressView.update(TxProgressView.TYPE_VOTE, result, txHash, null, proposalId, null, voteFormView.getValue(), NumberFormatter.getNumber(feeFormView.getValueOrigin()), "", address);
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
                showVoteFormView(true);
                break;
            case 2:
                step = 1;
                showFeeFormView(true);
                break;
            case 3:
                step = 2;
                showPasswordFormView(true);
                break;
        }
    }

    private View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (step) {
                case 0:
                    step = 1;
                    showFeeFormView(false);
                    break;
                case 1:
                    if (feeFormView != null) varifyFee(feeFormView.getValueOrigin());
                    break;
                case 2:
                    if (passwordFormView != null) varifyPassword(passwordFormView.getValue());
                    break;
                case 3:
                    step = 4;
                    showTxProgressView();
                    getAccountStatus();
                    break;
                case 4:
                    if (!isSending) finish();
                    break;
            }
        }
    };
}
