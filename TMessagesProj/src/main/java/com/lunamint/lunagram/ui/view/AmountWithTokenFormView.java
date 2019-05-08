package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.AmountEditText;
import com.lunamint.wallet.model.Coin;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.math.BigDecimal;

public class AmountWithTokenFormView extends LinearLayout {

    private boolean isEditable;
    private String plusAmounts[] = {"0.01", "0.1", "1", "10", "100"};

    private AmountEditText editText;
    private SendCoinTokenCardView sendCoinTokenCardView;

    private Button plusButtons[];

    public AmountWithTokenFormView(Context context, boolean isEditable, Coin coin, String defaultAmount, OnClickListener onClickListener) {
        super(context);

        this.isEditable = isEditable;

        setOrientation(VERTICAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("amount", R.string.amount));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(LocaleController.getString("enterAmount", R.string.enterAmount));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 32));

        TextView selectTokenTextview = new TextView(context);
        selectTokenTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        selectTokenTextview.setTextSize(0, AndroidUtilities.dp(16));
        selectTokenTextview.setTypeface(Typeface.DEFAULT_BOLD);
        selectTokenTextview.setText(LocaleController.getString("selectToken", R.string.selectToken));
        addView(selectTokenTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        sendCoinTokenCardView = new SendCoinTokenCardView(context, coin, onClickListener);
        addView(sendCoinTokenCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        TextView amountTextview = new TextView(context);
        amountTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        amountTextview.setTextSize(0, AndroidUtilities.dp(16));
        amountTextview.setTypeface(Typeface.DEFAULT_BOLD);
        amountTextview.setText(LocaleController.getString("amount", R.string.amount));
        addView(amountTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        editText = new AmountEditText(context, isEditable, defaultAmount, null);
        addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout plusButtonLayout = new LinearLayout(context);
        plusButtonLayout.setOrientation(HORIZONTAL);
        addView(plusButtonLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int rightMargin = 10;
        plusButtons = new Button[plusAmounts.length];
        for (int i = 0; plusButtons.length > i; i++) {
            plusButtons[i] = new Button(context);
            plusButtons[i].setId(i);
            plusButtons[i].setBackgroundResource(R.drawable.btn_amount_plus);
            plusButtons[i].setTextColor(ActivityCompat.getColor(context, R.color.medium_slate_blue));
            plusButtons[i].setTextSize(0, AndroidUtilities.dp(12));
            plusButtons[i].setText("+" + plusAmounts[i]);
            plusButtons[i].setPadding(0, 0, 0, 0);
            plusButtons[i].setOnClickListener(onClickPlusAmountListener);

            if (i == plusButtons.length - 1) rightMargin = 0;
            plusButtonLayout.addView(plusButtons[i], LayoutHelper.createLinear(0, 24, 1.0f, 0, 0, rightMargin, 0));
        }
    }

    public void showError(String msg) {
        if (editText != null) editText.showError(msg);
    }

    public String getValue() {
        if (editText == null) {
            return "";
        } else {
            return editText.getText();
        }
    }

    public String getValueOrigin() {
        if (editText == null) {
            return "";
        } else {
            return editText.getTextOrigin();
        }
    }

    public void setFocus() {
        if (editText != null) editText.setFocus();
    }

    public void clear() {
        if (editText != null) editText.clear();
    }

    public void update(Coin coin) {
        if (sendCoinTokenCardView != null) sendCoinTokenCardView.update(coin);
    }

    public int getCardPositionY() {
        if (sendCoinTokenCardView == null) return 0;
        int y = 0;
        try {
            Rect offsetViewBounds = new Rect();
            sendCoinTokenCardView.getDrawingRect(offsetViewBounds);
            offsetDescendantRectToMyCoords(sendCoinTokenCardView, offsetViewBounds);
            y = offsetViewBounds.top - AndroidUtilities.dp(10);
        } catch (Exception e) {
            // ignore
        }

        return y;
    }

    private OnClickListener onClickPlusAmountListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isEditable || editText == null || plusButtons == null || plusButtons[0] == null)
                return;

            BigDecimal amount;
            try {
                if (editText.getTextOrigin().length() == 0) {
                    amount = new BigDecimal("0");
                } else {
                    amount = new BigDecimal(editText.getTextOrigin());
                }

                amount = amount.add(new BigDecimal(plusAmounts[v.getId()]));
                editText.setText(String.format("%.4f", amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
