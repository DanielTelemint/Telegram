package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.DefaultEditText;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class DefaultFormView extends LinearLayout {

    private DefaultEditText editText;

    public DefaultFormView(Context context, boolean isEditable, boolean isPassword, boolean isVisible, String title, String desc, String text, String hint) {
        super(context);

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
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 40));

        editText = new DefaultEditText(context, isEditable, isPassword, isVisible, text, hint);
        addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));
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

    public void setValue(String value){
        if (editText != null) editText.setText(value);
    }

    public void setFocus() {
        if (editText != null) editText.setFocus();
    }

    public void clearFocus() {
        if (editText != null) editText.clearFocus();
    }

    public void clear() {
        if (editText != null) editText.clear();
    }


}
