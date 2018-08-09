package com.telemint.ui.cell;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telemint.messenger.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class DefaultInfoCell extends LinearLayout {

    private TextView fieldTitleTextview;
    private TextView fieldValueTextview;

    public DefaultInfoCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        addView(textLayout, LayoutHelper.createLinear(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1, Gravity.NO_GRAVITY));

        ImageView arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.list_arrow);
        arrow.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(arrow);

        fieldTitleTextview = new TextView(context);
        fieldTitleTextview.setTextColor(getResources().getColor(R.color.text_title_default));
        fieldTitleTextview.setTextSize(0, AndroidUtilities.dp(14));
        textLayout.addView(fieldTitleTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, Gravity.NO_GRAVITY,0,8,0,0));

        fieldValueTextview = new TextView(context);
        fieldValueTextview.setTextColor(getResources().getColor(R.color.text_title_default));
        fieldValueTextview.setTextSize(0, AndroidUtilities.dp(10));
        textLayout.addView(fieldValueTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, Gravity.NO_GRAVITY,0,0,0,8));


    }

    public void update(String title, String value){
        fieldTitleTextview.setText(title);
        fieldValueTextview.setText(value);
    }


}
