package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.SecureKeyStore;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.wallet.WalletManager;
import com.lunamint.wallet.model.CmdResult;
import com.lunamint.wallet.utils.AnimUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.Components.LayoutHelper;

public class DeleteAccountActivity extends LunagramBaseActivity {

    private String accountName;

    private ScrollView scrollView;
    private FrameLayout contentsLayout;
    private DefaultFormView passwordFormView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("deleteWallet", R.string.deleteWallet));

        accountName = getIntent().getStringExtra("accountName");

        RelativeLayout mainLayout = new RelativeLayout(this);

        scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        contentsLayout = new FrameLayout(this);
        contentsLayout.setPadding(0, 0, 0, AndroidUtilities.dp(70));
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nextButton = new DefaultButton(this, 12, true, LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue, onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));
        ((RelativeLayout.LayoutParams) nextButton.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        setContentView(mainLayout);

        showPasswordFormView(false);
        if (hasPassword() && isEnabledFingerprint(accountName)) checkFingerprint();
    }

    @Override
    protected void onPause() {
        clearForm();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case WalletManager.DELETE_ACCOUNT:
                onDeleteAccountResult(cmdResult);
                break;
        }
    }

    private void deleteAccount() {
        if (passwordFormView == null || accountName == null || accountName.equals("")) return;
        showProgress();
        WalletManager.getInstance().deleteAccount(new ResultHandler(this), accountName, passwordFormView.getValue());
    }

    private void clearForm() {
        if (passwordFormView != null) passwordFormView.clear();
    }

    private void onDeleteAccountResult(CmdResult cmdResult) {
        hideProgress();

        clearForm();

        if (cmdResult == null) {
            if (passwordFormView != null)
                passwordFormView.showError(LocaleController.getString("unknownError", R.string.unknownError));
        } else if (cmdResult.getErrMsg() != null) {
            if (cmdResult.getErrMsg().contains("Key deleted forever")) {
                SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                pref.edit().remove("W-" + accountName.replaceAll(" ", "")).commit();

                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.accountChanged);

                finish();
            } else if (cmdResult.getErrMsg().contains("invalid account password")) {
                if (passwordFormView != null)
                    passwordFormView.showError(LocaleController.getString("incorrectPassword", R.string.incorrectPassword));
            }
            cmdResult.clear();
        }
    }

    private void varifyPassword(String pwd) {
        if (passwordFormView == null) return;
        if (pwd == null || pwd.length() < 8) {
            passwordFormView.showError(LocaleController.getString("pwdInvalidError", R.string.pwdInvalidError));
        } else {
            deleteAccount();
        }
    }

    private void showPasswordFormView(boolean isMoveToBack) {
        if (passwordFormView == null) {
            passwordFormView = new DefaultFormView(DeleteAccountActivity.this, true, true, false, LocaleController.getString("setPassword", R.string.setPassword), LocaleController.getString("enterPassword", R.string.enterPassword), null, null);
            contentsLayout.addView(passwordFormView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        if (isMoveToBack) {
            AnimUtil.changePrevView(passwordFormView, currentView, false);
        } else {
            AnimUtil.changeView(currentView, passwordFormView, false);
        }
        if (!isEnabledFingerprint(accountName)) passwordFormView.setFocus();
        currentView = passwordFormView;

        nextButton.update(LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue);
    }

    private boolean hasPassword() {
        SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String pwd = pref.getString("W-" + accountName.replaceAll(" ", ""), "");
        return !pwd.equals("");
    }

    private View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (passwordFormView != null)
                varifyPassword(passwordFormView.getValue());
        }
    };
}
