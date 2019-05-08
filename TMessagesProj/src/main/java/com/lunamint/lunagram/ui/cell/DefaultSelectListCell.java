package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class DefaultSelectListCell extends LinearLayout {

    private TextView nameTextview;

    public DefaultSelectListCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(10));

        nameTextview = new TextView(context);
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        nameTextview.setTextSize(0, AndroidUtilities.dp(16));
        addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }


    public void update(String name) {
        nameTextview.setText(name);
    }


}
