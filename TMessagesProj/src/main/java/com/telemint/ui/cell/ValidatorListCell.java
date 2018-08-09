package com.telemint.ui.cell;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telemint.messenger.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class ValidatorListCell extends LinearLayout {

    private TextView fieldTitleTextview;
    private TextView fieldValueTextview;

    public ValidatorListCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        ImageView profileImageview = new ImageView(context);
        profileImageview.setBackgroundColor(context.getResources().getColor(R.color.tab_text_selected));
        addView(profileImageview, LayoutHelper.createLinear(26, 26, 0,0,10,0));

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        addView(textLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        ImageView arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.list_arrow);
        arrow.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(arrow);

        fieldTitleTextview = new TextView(context);
        fieldTitleTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
        fieldTitleTextview.setTextSize(0, AndroidUtilities.dp(14));
        textLayout.addView(fieldTitleTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,15,0,0));

        fieldValueTextview = new TextView(context);
        fieldValueTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
        fieldValueTextview.setTextSize(0, AndroidUtilities.dp(10));
        textLayout.addView(fieldValueTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,0,0,15));

    }

    public void update(int position, String name, String fee, String voting_power){
        fieldTitleTextview.setText("#" + (position+1) + " - " + name);
        fieldValueTextview.setText("Fee - " + fee + " / VotingPower - " + voting_power);
    }


}
