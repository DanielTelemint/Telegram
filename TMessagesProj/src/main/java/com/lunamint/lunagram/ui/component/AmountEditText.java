package com.lunamint.lunagram.ui.component;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.utils.BigDecimalUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

import java.math.BigDecimal;

public class AmountEditText extends LinearLayout {

    private boolean isError = false;

    private EditText editText;
    private TextView errorTextview;

    public AmountEditText(Context context, boolean isEditable, String defaultText, String hint) {
        super(context);

        setOrientation(VERTICAL);

        editText = new EditText(context);
        editText.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        editText.setTextSize(0, AndroidUtilities.dp(16));
        editText.setBackgroundResource(R.drawable.bg_edittext);
        editText.setFocusable(isEditable);
        editText.setOnKeyListener(onKeyListener);
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (defaultText != null) editText.setText(defaultText);
        if (hint != null) editText.setHint(hint);
        addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        errorTextview = new TextView(context);
        errorTextview.setTextColor(ActivityCompat.getColor(context, R.color.coral_red));
        errorTextview.setTextSize(0, AndroidUtilities.dp(12));
        addView(errorTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    public void showError(String msg) {
        if (editText == null) return;
        isError = true;
        editText.setBackgroundResource(R.drawable.bg_edittext_error);
        errorTextview.setText(msg);
    }

    public String getText() {
        if (editText == null) {
            return "0";
        } else {
            if (editText.getText().toString().length() == 0) {
                return "0";
            } else {
                return BigDecimalUtil.getNumberOrigin(editText.getText().toString(), "0");
            }
        }
    }

    public String getTextOrigin() {
        if (editText == null) {
            return "0";
        } else {
            if (editText.getText().toString().length() == 0) {
                return "";
            }else{
                BigDecimal amount = new BigDecimal(editText.getText().toString());
                return String.format("%.4f", amount);
            }
        }
    }

    public void setText(String text) {
        if (editText == null) return;
        editText.setText(text);
    }

    public void clear() {
        if (editText == null) return;
        editText.setText("");
    }

    public void setFocus() {
        if (editText != null) {
            editText.requestFocus();
            showKeyboard();
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (isError) {
                isError = false;
                if (editText != null) editText.setBackgroundResource(R.drawable.bg_edittext);
            }
            return false;
        }
    };
}
