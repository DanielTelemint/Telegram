package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.AccountInfo;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class AccountListCell extends LinearLayout {

    private TextView nameTextview;
    private TextView addressTextview;

    public AccountListCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20));

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(textLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 10, 0));

        nameTextview = new TextView(context);
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        nameTextview.setTextSize(0, AndroidUtilities.dp(16));
        textLayout.addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        addressTextview = new TextView(context);
        addressTextview.setTextSize(0, AndroidUtilities.dp(10));
        addressTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        textLayout.addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        ImageView arrowImgview = new ImageView(getContext());
        arrowImgview.setImageResource(R.drawable.ic_arrow_default);
        arrowImgview.setAlpha(0.6f);
        addView(arrowImgview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    public void update(AccountInfo data) {
        nameTextview.setText(data.getName());
        addressTextview.setText(data.getAddress());
    }
}
