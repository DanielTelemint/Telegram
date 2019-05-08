package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Coin;
import com.lunamint.wallet.utils.NumberFormatter;
import com.lunamint.wallet.utils.TokenUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class TokenListCell extends LinearLayout {

    private ImageView tokenImageview;
    private TextView nameTextview;
    private TextView amountTextview;

    public TokenListCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));

        tokenImageview = new ImageView(context);
        addView(tokenImageview, LayoutHelper.createLinear(40, 40, 0, 0, 10, 0));

        nameTextview = new TextView(context);
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        nameTextview.setTextSize(0, AndroidUtilities.dp(16));
        addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 12, 0));

        amountTextview = new TextView(context);
        amountTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        amountTextview.setTextSize(0, AndroidUtilities.dp(14));
        amountTextview.setGravity(Gravity.RIGHT);
        addView(amountTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));
    }

    public void update(Coin data) {
        if (nameTextview == null) return;
        tokenImageview.setImageResource(TokenUtil.getTokenIcon(data.getDenom()));
        nameTextview.setText(data.getDenomDisplayName());
        amountTextview.setText(NumberFormatter.getNumber(data.getAmount()));
    }
}
