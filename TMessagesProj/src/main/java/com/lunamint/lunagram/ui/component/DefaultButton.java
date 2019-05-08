package com.lunamint.lunagram.ui.component;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.widget.Button;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class DefaultButton extends CardView {

    private Button button;

    public DefaultButton(Context context, int radius, boolean isElevation, String name, int backgroundResource, OnClickListener onClickListener) {
        super(context);

        if(isElevation){
            setUseCompatPadding(true);
            setRadius(AndroidUtilities.dp(radius));
            setCardElevation(UiUtil.getDefaultElevation());
        }

        button = new Button(context);
        button.setBackgroundResource(backgroundResource);
        button.setTextSize(0, AndroidUtilities.dp(16));
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setTextColor(ActivityCompat.getColor(context, R.color.white));
        int padding = AndroidUtilities.dp(18);
        button.setPadding(padding, padding, padding, padding);
        button.setText(name);
        button.setOnClickListener(onClickListener);
        addView(button, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void update(String name, int backgroundResource) {
        if (button == null) return;
        button.setText(name);
        button.setBackgroundResource(backgroundResource);
    }
}
