package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.SecureKeyStore;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.lunagram.ui.view.CreateWalletWarningView;
import com.lunamint.lunagram.ui.view.SeedConfirmView;
import com.lunamint.lunagram.ui.view.SelectWalletTypeFormView;
import com.lunamint.lunagram.ui.view.TelegramTransferOnboardingView;
import com.lunamint.wallet.model.AccountInfo;
import com.lunamint.wallet.model.AccountInfoWithSeed;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.utils.AnimUtil;
import com.lunamint.wallet.utils.Parser;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class CreateAccountActivity extends LunagramBaseActivity {

    public final static int TYPE_CREATE_NEW_ACCOUNT = 0;
    public final static int TYPE_IMPORT_EXISTING_WALLET = 1;

    private int step = 0;

    private int type = -1;

    private boolean isFirstAccount = false;

    private String seed;
    private String accountName = "";

    private ScrollView scrollView;
    private FrameLayout contentsLayout;
    private SelectWalletTypeFormView selectWalletTypeFormView;
    private DefaultFormView walletNameFormView;
    private DefaultFormView seedFormView;
    private DefaultFormView passwordFormView;
    private DefaultFormView repeatPasswordFormView;
    private CreateWalletWarningView warningView;
    private SeedConfirmView seedConfirmView;
    private TelegramTransferOnboardingView telegramTransferOnboardingView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("createWallet", R.string.createWallet));
        if(getWindow() != null) getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        isFirstAccount = getIntent().getBooleanExtra("isFirstAccount", false);

        RelativeLayout mainLayout = new RelativeLayout(this);

        scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        contentsLayout = new FrameLayout(this);
        contentsLayout.setPadding(0, 0, 0, AndroidUtilities.dp(70));
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        selectWalletTypeFormView = new SelectWalletTypeFormView(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateWallet(v.getId());
            }
        });
        contentsLayout.addView(selectWalletTypeFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nextButton = new DefaultButton(this, 12, true, LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue, onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));
        ((RelativeLayout.LayoutParams) nextButton.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        nextButton.setVisibility(View.INVISIBLE);

        currentView = selectWalletTypeFormView;

        setContentView(mainLayout);
    }

    @Override
    protected void onPause() {
        clearForm();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (seedFormView != null) seedFormView.clear();
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
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        CmdResult cmdResult = ((CmdResult) msg.getData().getSerializable("result"));
        switch (msg.what) {
            case WalletManager.GET_ACCOUNT_LIST:
                onGetAccountListResult(cmdResult);
                break;
            case WalletManager.CREATE_ACCOUNT:
                onCreateAccountResult(cmdResult);
                break;
        }
    }

    private void startCreateWallet(int type) {
        this.type = type;

        if (this.type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
            showWalletNameForm(false);
        } else {
            showWarningSeedView(false);
        }
        if (selectWalletTypeFormView != null) contentsLayout.removeView(selectWalletTypeFormView);
        selectWalletTypeFormView = null;
    }

    private void createAccount() {
        if (walletNameFormView == null) return;
        showProgress();
        if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
            WalletManager.getInstance().createAccount(new ResultHandler(this), walletNameFormView.getValue(), passwordFormView.getValue());
        } else {
            WalletManager.getInstance().createAccount(new ResultHandler(this), seedFormView.getValue().replaceAll("\n", ""), walletNameFormView.getValue(), passwordFormView.getValue());
        }
    }

    private void clearForm() {
        if (passwordFormView != null) passwordFormView.clear();
        if (repeatPasswordFormView != null) repeatPasswordFormView.clear();
    }

    private void onGetAccountListResult(CmdResult cmdResult) {
        String err_msg = null;

        if (cmdResult == null) {
            err_msg = LocaleController.getString("unknownError", R.string.unknownError);
        } else if (cmdResult.getErrMsg() != null && !cmdResult.getErrMsg().contains("no such file or directory")) {
            err_msg = cmdResult.getErrMsg();
        } else {
            String requestName = accountName.replaceAll(" ", "");
            ArrayList<AccountInfo> accountList = Parser.getAccountList(cmdResult.getData());
            for (AccountInfo accountInfo : accountList) {
                String name = accountInfo.getName().replaceAll(" ", "");
                if (name.equals(requestName)) {
                    err_msg = LocaleController.getString("accountNameInvalidError", R.string.accountNameInvalidError);
                }
            }
        }

        if (err_msg == null) {
            if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
                step = 1;
            } else {
                step = 3;
            }
            showPasswordFormView(false);

        } else {
            if (walletNameFormView != null) walletNameFormView.showError(err_msg);
        }
        hideProgress();
    }

    private void onCreateAccountResult(CmdResult cmdResult) {
        hideProgress();
        if (cmdResult == null) {
            if (repeatPasswordFormView != null)
                repeatPasswordFormView.showError(LocaleController.getString("unknownError", R.string.unknownError));
        } else if (cmdResult.getErrMsg() != null) {
            if (repeatPasswordFormView != null)
                repeatPasswordFormView.showError(cmdResult.getErrMsg());
        } else {
            AccountInfoWithSeed accountInfo;
            if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
                accountInfo = Parser.getAccountInfoWithSeed(cmdResult.getData(), true);
            } else {
                accountInfo = Parser.getAccountInfoWithSeed(cmdResult.getData(), false);
            }
            if (accountInfo != null) {
                hideKeyboard();
                if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
                    step = 3;
                    seed = accountInfo.getSeed();
                    showWarningSeedView(false);
                } else {
                    step = 5;
                    showOnboardingView(false);
                }

                if (Build.VERSION.SDK_INT >= 23 && SecureKeyStore.getInstance().isSupported()) {
                    try {
                        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                        if (fingerprintManager.isHardwareDetected()) {
                            String encrypted = SecureKeyStore.getInstance().encrypt(getApplicationContext(), passwordFormView.getValue());
                            SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                            pref.edit().putString("W-" + accountInfo.getName().replaceAll(" ", ""), encrypted).commit();
                        }
                    } catch (Throwable e) {
                        // ignore
                    }
                }

                if (isFirstAccount)
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.accountChanged);
            } else {
                if (repeatPasswordFormView != null)
                    repeatPasswordFormView.showError(LocaleController.getString("unknownError", R.string.unknownError));
            }

            cmdResult.clear();
        }

        clearForm();
    }

    private void varifyAccountName(String accountName) {
        showProgress();
        if (accountName == null || accountName.length() <= 0) {
            if (walletNameFormView != null)
                walletNameFormView.showError(LocaleController.getString("accountNameEmptyError", R.string.accountNameEmptyError));
            hideProgress();
            return;
        }
        this.accountName = accountName;
        WalletManager.getInstance().getAccountList(new ResultHandler(this));
    }

    private void varifySeed(String seed) {
        if (seed == null || seed.length() == 0) {
            if (seedFormView != null)
                seedFormView.showError(LocaleController.getString("seedInvalidError", R.string.seedInvalidError));
        } else {
            step = 2;
            showWalletNameForm(false);
        }
    }

    private void varifyPassword(String pwd) {
        if (passwordFormView == null) return;
        if (pwd == null || pwd.length() < 8) {
            passwordFormView.showError(LocaleController.getString("pwdInvalidError", R.string.pwdInvalidError));
        } else {
            if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
                step = 2;
            } else {
                step = 4;
            }
            showPasswordRepeatFormView(false);
        }
    }

    private void varifyRepeatPassword(String pwd) {
        if (passwordFormView == null) return;
        if (!passwordFormView.getValue().equals(pwd)) {
            if (repeatPasswordFormView != null)
                repeatPasswordFormView.showError(LocaleController.getString("passwordNotMatchError", R.string.passwordNotMatchError));
        } else {
            createAccount();
        }
    }

    private void showSeedForm(boolean isMoveToBack) {
        if (seedFormView == null) {
            seedFormView = new DefaultFormView(this, true, true, true, LocaleController.getString("inputWordSeed", R.string.inputWordSeed), LocaleController.getString("inputSeedDesc", R.string.inputSeedDesc), null, null);
            contentsLayout.addView(seedFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(seedFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, seedFormView, false);
        }
        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        seedFormView.setFocus();
        currentView = seedFormView;
    }

    private void showWalletNameForm(boolean isMoveToBack) {
        if (walletNameFormView == null) {
            walletNameFormView = new DefaultFormView(this, true, false, false, LocaleController.getString("walletName", R.string.walletName), LocaleController.getString("chooseYourWallet", R.string.chooseYourWallet), null, null);
            contentsLayout.addView(walletNameFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
        if (isMoveToBack) {
            AnimUtil.changePrevView(walletNameFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, walletNameFormView, false);
            if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT)
                AnimUtil.showView(nextButton);
        }

        walletNameFormView.setFocus();
        currentView = walletNameFormView;
    }

    private void showPasswordFormView(boolean isMoveToBack) {
        if (passwordFormView == null) {
            passwordFormView = new DefaultFormView(CreateAccountActivity.this, true, true, false, LocaleController.getString("setPassword", R.string.setPassword), LocaleController.getString("enterPassword", R.string.enterPassword), null, null);
            contentsLayout.addView(passwordFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(passwordFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, passwordFormView, false);
        }
        passwordFormView.setFocus();
        currentView = passwordFormView;

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
    }

    private void showPasswordRepeatFormView(boolean isMoveToBack) {
        if (repeatPasswordFormView == null) {
            repeatPasswordFormView = new DefaultFormView(this, true, true, false, LocaleController.getString("repeatPassword", R.string.repeatPassword), LocaleController.getString("doubleCheckPassword", R.string.doubleCheckPassword), null, null);
            contentsLayout.addView(repeatPasswordFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(repeatPasswordFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, repeatPasswordFormView, false);
        }
        repeatPasswordFormView.setFocus();
        currentView = repeatPasswordFormView;

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
    }

    private void showWarningSeedView(boolean isMoveToBack) {
        if (warningView == null) {
            if (type == CreateAccountActivity.TYPE_IMPORT_EXISTING_WALLET) {
                AnimUtil.showView(nextButton);
            }
            warningView = new CreateWalletWarningView(this, type);
            contentsLayout.addView(warningView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(warningView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, warningView, false);
        }

        currentView = warningView;

        nextButton.update(LocaleController.getString("iUnderstand", R.string.iUnderstand), R.drawable.btn_radius4_red);
    }

    private void showSeedConfirmView(boolean isMoveToBack) {
        if (seedConfirmView == null) {
            seedConfirmView = new SeedConfirmView(this, seed);
            contentsLayout.addView(seedConfirmView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(seedConfirmView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, seedConfirmView, false);
        }
        currentView = seedConfirmView;

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
    }

    private void showOnboardingView(boolean isMoveToBack) {
        if (telegramTransferOnboardingView == null) {
            telegramTransferOnboardingView = new TelegramTransferOnboardingView(this);
            contentsLayout.addView(telegramTransferOnboardingView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(telegramTransferOnboardingView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, telegramTransferOnboardingView, false);
        }

        currentView = telegramTransferOnboardingView;

        nextButton.update(LocaleController.getString("done", R.string.done), R.drawable.btn_radius4_blue);
    }

    private void moveToBack() {
        if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
            switch (step) {
                case 0:
                    finish();
                    break;
                case 1:
                    step = 0;
                    showWalletNameForm(true);
                    break;
                case 2:
                    step = 1;
                    showPasswordFormView(true);
                    break;
                case 5:
                    finish();
                    break;
            }
        } else {
            switch (step) {
                case 0:
                    finish();
                    break;
                case 1:
                    step = 0;
                    showWarningSeedView(true);
                    break;
                case 2:
                    step = 1;
                    showSeedForm(true);
                    break;
                case 3:
                    step = 2;
                    showWalletNameForm(true);
                    break;
                case 4:
                    step = 3;
                    showPasswordFormView(true);
                    break;
                case 5:
                    finish();
                    break;
            }
        }
    }

    private View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (type == CreateAccountActivity.TYPE_CREATE_NEW_ACCOUNT) {
                switch (step) {
                    case 0:
                        if (walletNameFormView != null)
                            varifyAccountName(walletNameFormView.getValue());
                        break;
                    case 1:
                        if (passwordFormView != null)
                            varifyPassword(passwordFormView.getValue());
                        break;
                    case 2:
                        if (repeatPasswordFormView != null)
                            varifyRepeatPassword(repeatPasswordFormView.getValue());
                        break;
                    case 3:
                        scrollView.smoothScrollTo(0, 0);
                        step = 4;
                        showSeedConfirmView(false);
                        seed = "";
                        break;
                    case 4:
                        scrollView.smoothScrollTo(0, 0);
                        step = 5;
                        showOnboardingView(false);
                        break;
                    case 5:
                        finish();
                        break;
                }
            } else {
                switch (step) {
                    case 0:
                        step = 1;
                        showSeedForm(false);
                        break;
                    case 1:
                        if (seedFormView != null)
                            varifySeed(seedFormView.getValue());
                        break;
                    case 2:
                        if (walletNameFormView != null)
                            varifyAccountName(walletNameFormView.getValue());
                        break;
                    case 3:
                        if (passwordFormView != null)
                            varifyPassword(passwordFormView.getValue());
                        break;
                    case 4:
                        if (repeatPasswordFormView != null)
                            varifyRepeatPassword(repeatPasswordFormView.getValue());
                        break;
                    case 5:
                        finish();
                        break;
                }
            }
        }
    };
}
