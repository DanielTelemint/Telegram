package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.AmountEditText;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.math.BigDecimal;

public class AmountFormView extends LinearLayout {

    private boolean isEditable;
    private String plusAmounts[] = {"0.01", "0.1", "1", "10", "100"};

    private CardView availableCardView;
    private TextView availableBalanceTextview;
    private AmountEditText editText;

    private Button plusButtons[];

    public AmountFormView(Context context, boolean isEditable, String title, String desc, Coin availableCoin, String defaultAmount) {
        super(context);

        this.isEditable = isEditable;

        setOrientation(VERTICAL);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(title);
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(desc);
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 32));

        availableCardView = new CardView(context);
        availableCardView.setBackgroundResource(R.drawable.bg_card_white);
        availableCardView.setUseCompatPadding(true);
        availableCardView.setRadius(AndroidUtilities.dp(8));
        availableCardView.setCardElevation(UiUtil.getDefaultElevation());

        addView(availableCardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        LinearLayout availableLayout = new LinearLayout(context);
        availableLayout.setOrientation(HORIZONTAL);
        availableLayout.setGravity(Gravity.CENTER_VERTICAL);
        availableLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(14), AndroidUtilities.dp(20), AndroidUtilities.dp(14));
        availableCardView.addView(availableLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView availableTitleTextview = new TextView(context);
        availableTitleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        availableTitleTextview.setTextSize(0, AndroidUtilities.dp(16));
        availableTitleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        availableTitleTextview.setText(LocaleController.getString("available", R.string.available));
        availableLayout.addView(availableTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        availableBalanceTextview = new TextView(context);
        availableBalanceTextview.setTextColor(ActivityCompat.getColor(context, R.color.medium_slate_blue));
        availableBalanceTextview.setTextSize(0, AndroidUtilities.dp(16));
        availableBalanceTextview.setTypeface(Typeface.DEFAULT_BOLD);
        availableBalanceTextview.setGravity(Gravity.RIGHT);
        availableLayout.addView(availableBalanceTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.f));

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

        update(availableCoin);
    }

    public void update(Coin coin) {
        if (availableBalanceTextview == null || coin == null) return;
        availableBalanceTextview.setText(NumberFormatter.getNumber(coin.getAmount()) + " " + coin.getDenomDisplayName());
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
