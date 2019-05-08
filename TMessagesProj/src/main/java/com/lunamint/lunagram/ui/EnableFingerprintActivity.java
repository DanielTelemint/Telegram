package com.lunamint.lunagram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.secure.SecureKeyStore;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.DefaultFormView;
import com.lunamint.wallet.utils.AnimUtil;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class EnableFingerprintActivity extends LunagramBaseActivity {


    private String accountName = "";

    private FrameLayout contentsLayout;
    private DefaultFormView passwordFormView;

    private DefaultButton nextButton;

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("enableFingerprint", R.string.enableFingerprint));

        accountName = getIntent().getStringExtra("accountName");

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        contentsLayout = new FrameLayout(this);
        scrollView.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        nextButton = new DefaultButton(this, 4, true, LocaleController.getString("next", R.string.next), R.drawable.btn_radius4_blue, onClickNextListener);
        mainLayout.addView(nextButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));

        setContentView(mainLayout);

        showPasswordFormView(false);
    }

    @Override
    protected void onPause() {
        clearPasswordForm();
        super.onPause();
    }

    private void clearPasswordForm() {
        if (passwordFormView == null) return;
        passwordFormView.clear();
    }

    private void varifyPassword(String pwd) {
        if (passwordFormView == null) return;
        if (pwd == null || pwd.length() < 8) {
            passwordFormView.showError(LocaleController.getString("pwdInvalidError", R.string.pwdInvalidError));
        } else {
            SharedPreferences pref = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            if (SecureKeyStore.getInstance().decrypt(getApplicationContext(), pref.getString("W-" + accountName.replaceAll(" ", ""), "")).equals(passwordFormView.getValue())) {
                setEnabledFingerprint(accountName, true);
                hideKeyboard();
                finish();
            } else {
                passwordFormView.showError(LocaleController.getString("incorrectPassword", R.string.incorrectPassword));
            }
        }
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
        passwordFormView.setFocus();
        currentView = passwordFormView;
    }

    private View.OnClickListener onClickNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (passwordFormView != null) varifyPassword(passwordFormView.getValue());
        }
    };
}
